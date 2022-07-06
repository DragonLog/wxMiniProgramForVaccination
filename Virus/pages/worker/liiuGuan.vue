<template>
	<view class="page">
		<u-button type="primary" @click="scanCode">扫码</u-button>
		<view v-show="isShow"  class="aaa">
			<text style="text-align: center; display: block; color: red; margin-top: 20rpx;">{{result.msg}}</text>
		</view>
		<view>
			<u-form v-show="result.code === 0" label-width='200'>
				<u-form-item label="留观开始时间">
					<text style="color: red;">{{result.data.createTime}}</text>
				</u-form-item>
				<u-form-item label="留观时长">
					<text style="color: red;">30分钟</text>
				</u-form-item>
				<u-form-item label="留观结束时间">
					<text style="color: red;">{{result.data.endTime}}</text>
				</u-form-item>
				<u-form-item label="备注">
					<u-input v-model="form.note"></u-input>
				</u-form-item>
		
				<u-button type="success" @click="liuGuan">结束留观</u-button>
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
				form:{
					appointId:"",
					note:""
				},
				isLoading:false
	// 			rules : {
	// 				liiuguanShichang:[
						
	// 									{ 
	// 										required: true, 
											
	// 										message: '请输入留观时间', 
											
	// 										// 可以单个或者同时写两个触发验证方式 
	// 										trigger: ['blur'],
	// 									},
									
	// 									{
	// 										pattern:/^[2-4]?[0-9]$/,
	// 										message:'时长应在20-50分钟之间',
	// 										trigger: 'blur',
	// 									}
	// 				]
	
	// 			}

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
						const id=JSON.parse(res.result);
						this.form.appointId = id;
						this.$u.post("/worker/observe/ok",{id}).then(result=>{
						if (result.code != 0) {
							this.isShow=true;
						}
						this.result = result;
						})
					}
				})
			},
			liuGuan(){
				this.isLoading=true;
				this.$u.put("/worker/observe/change",this.form).then(res=>{
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
