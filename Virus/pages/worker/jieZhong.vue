<template>
	<view class="page">
		<u-button type="primary" @click="scanCode">扫码</u-button>
		<view v-show="isShow" class="aaa">
			<text style="text-align: center; display: block; color: red; margin-top: 20rpx;">{{result.msg}}</text>
		</view>
		<view style="margin-top: 10rpx;">
			<u-form v-show="result.code === 0" label-width='150'>
				<u-form-item label="接种部位">
					<u-radio-group v-model='form.part' >
						<u-radio name='左臂'>左臂</u-radio>
						<u-radio name='右臂'>右臂</u-radio>
						<u-radio name='左腿'>左腿</u-radio>
						<u-radio name='右腿'>右腿</u-radio>
						<u-radio name='臀部'>臀部</u-radio>
					</u-radio-group>
				</u-form-item>
				<u-form-item label="疫苗批号">
					<u-input v-model="form.vaccineBatchCode"></u-input>
				</u-form-item>
				<u-form-item label="备注">
					<u-input v-model="form.note"></u-input>
				</u-form-item>
				<u-button :loading="isLoading" type="success" @click="jieZhong(0)">接种完成</u-button>
				<button :loading="isLoading" type="warn" @click="jieZhong(1)" style="margin-top: 10rpx;">接种未完成</button>
			</u-form>
			</view>
			<u-toast ref="uToast" />
	</view>
</template>

<script>
	export default {
		data() {
			return {
				result:{},
				isShow:false,
				isLoading:false,
				form:{
					appointId:"",
					part:"左臂",
					vaccineBatchCode:"",
					note:""
				}
				// list:[],
				// show:false
				// rules : {
				// 	jiezhongbuwei:[
						
				// 						{ 
				// 							required: true, 
											
				// 							message: '请选择接种部位', 
											
				// 							// 可以单个或者同时写两个触发验证方式 
				// 							trigger: ['blur'],
				// 						},
										
				// 	],
				// 	yimiaoPihao:[
						
				// 						{ 
				// 							required: true, 
											
				// 							message: '请输入疫苗批号', 
											
				// 							// 可以单个或者同时写两个触发验证方式 
				// 							trigger: ['blur'],
				// 						},
										
				// 	],
				// 	yimiaoZhonglei:[
						
				// 						{ 
				// 							required: true, 
											
				// 							message: '请选择疫苗种类', 
											
				// 							// 可以单个或者同时写两个触发验证方式 
				// 							trigger: ['blur'],
				// 						},
										
				// 	]
					
				// }
			}
		},
		onReady() {
			// this.$refs.uForm.setRules(this.rules);
		},
		methods: {
			scanCode(){
				this.isShow=false;
				uni.scanCode({
					onlyFromCamera: false,
					scanType: ['qrCode'],
					success: res=>{
						// this.form.yuyueId=JSON.parse(res.result);
						// this.getYimaoList();
						const id=JSON.parse(res.result);
						this.form.appointId = id;
						this.$u.post("/worker/inoculate/ok",{id}).then(result=>{
						if (result.code != 0) {
							this.isShow=true;
						}
						this.result = result;
						})
					}
				})
			},
			// getYimaoList(){
			// 	this.$u.get('/yimiao/pageQuery').then(res=>{
			// 		console.log(res)
			// 			this.list=res.data.map(item=>{
			// 				console.log(res)
			// 				return {
								
			// 					label:item.yimiaoShengchanqiye+item.yimiaoZhonglei,
							
			// 					extra:item
			// 				}
			// 			})
					
					
			// 	})				
			// },
			jieZhong(isSucceeded){
				this.isLoading=true;
				this.$u.post("/worker/inoculate/save?isSucceeded=" + isSucceeded ,this.form).then(res=>{
					if(res.code == 0){
						this.$refs.uToast.show({
							title :res.msg,
							type : 'success'
						})
					}else{
						this.$refs.uToast.show({
							title :res.msg,
							type : 'error'
						})
					}
				}).finally(()=>{
					this.isLoading=false;
				})
			}
			// setYimiao(res){
			// 				this.form.yimiaoId = res[0].extra.id;
			// 				this.form.yimiaoShengchanqiye = res[0].extra.yimiaoShengchanqiye;
			// 				this.form.yimiaoZhonglei = res[0].extra.yimiaoZhonglei;
			// 				this.form.chengjishu = res[0].extra.zhenshu;
			// 			}
			
		}
	}
</script>

<style>
	.aaa{
		font-size: 50rpx;
	}
.page{
		padding: 50rpx;
		
	}
</style>
