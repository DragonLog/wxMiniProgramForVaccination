<template>
	<view class="page">
		<u-button type="primary" @click="scanCode">扫码</u-button>
		<view v-show="isShow"  class="aaa">
			<text style="text-align: center; display: block; color: red; margin-top: 20rpx;">{{result.msg}}</text>
		</view>
		<view style="margin-top: 10rpx;">
			<u-form v-show="result.code === 0" label-width='150'>
				<u-form-item label="低压">
					<u-input v-model="form.bloodPressureLow"></u-input>
				</u-form-item>
				<u-form-item label="高压">
					<u-input v-model="form.bloodPressureHigh"></u-input>
				</u-form-item>
				<u-form-item label="体温">
					<u-input v-model="form.temperature"></u-input>
				</u-form-item>
				<u-form-item label="是否服药">
					<u-radio-group v-model="form.isMedicine">
						<u-radio :name='0'>未服药</u-radio>
						<u-radio :name='1'>已服药</u-radio>
					</u-radio-group>
					<!-- <u-input v-model="form.isMedicine"></u-input> -->
				</u-form-item>
				<u-form-item label="药物名称" v-show="form.isMedicine==1">
					<u-input v-model="form.medicine"></u-input>
				</u-form-item>
				<u-form-item label="是否禁忌症">
					<u-radio-group v-model="form.isContraindication">
						<u-radio :name='0'>无禁忌症</u-radio>
						<u-radio :name='1'>有禁忌症</u-radio>
					</u-radio-group>
					<!-- <u-input v-model="form.isMedicine"></u-input> -->
				</u-form-item>
				<u-form-item label="禁忌症名称" v-show="form.isContraindication==1">
					<u-input v-model="form.contraindication"></u-input>
				</u-form-item>
				<u-form-item label="备注">
					<u-input v-model="form.note"></u-input>
				</u-form-item>
				<u-button :loading="isLoading" type="success" @click="yujian(0)">适宜接种</u-button>
				<button :loading="isLoading" type="warn" @click="yujian(1)" style="margin-top: 10rpx;">不适宜接种</button>
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
					isMedicine:0,
					medicine:"",
					isContraindication:0,
					contraindication:"",
					bloodPressureHigh:"",
					bloodPressureLow:"",
					temperature:"",
					appointId:"",
					isLoading:false,
					note:""
				}
				// rules : {
				// 	diya:[
						
				// 						{ 
				// 							required: true, 
				// 							message: '请输入血压', 
				// 							// 可以单个或者同时写两个触发验证方式 
				// 							trigger:'blur',
				// 						},
				// 						{
				// 							pattern:/^[6-8]?[0-9]$/,
				// 							message:'低压范围在60-90之间，如果不在此区间，建议咨询医生后在接种',
				// 							trigger: 'blur',
				// 						}
										
				// 	],
				// 	gaoya:[
				// 					{ 
				// 						required: true, 
				// 						message: '请输入血压', 
				// 						// 可以单个或者同时写两个触发验证方式 
				// 						trigger: 'blur',
				// 					},
				// 					{
				// 						pattern:/^9[0-9]|1[0-5][0-9]$/,
				// 						message:'低压范围在90-160之间，如果不在此区间，建议咨询医生后在接种',
				// 						trigger: ['change','blur'],
				// 					}
								
				// 	],
				// 	tiwen:[
				// 					{ 
				// 						required: true, 
				// 						message: '请输入体温', 
				// 						// 可以单个或者同时写两个触发验证方式 
				// 						trigger: ['blur'],
				// 					},
													
				// 					{
				// 							pattern:/^3[4-7]\.?[0-9]$/,
				// 							message:'您的体温异常，建议咨询医生后在接种',
				// 							trigger: ['change','blur'],
				// 						},
										
				// 	],
					
					
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
						// this.form.yuyueId=JSON.parse(res.result).id;
						const id=JSON.parse(res.result);
						this.form.appointId = id;
						this.$u.post("/worker/preCheck/ok",{id}).then(result=>{
						if (result.code != 0) {
							this.isShow=true;
						}
						this.result = result;
						})
					}
				})
			},
			yujian(isSuited){
				this.isLoading=true;
				this.$u.post("/worker/preCheck/save?isSuited=" + isSuited ,this.form).then(res=>{
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
				// this.$refs.uForm.validate(valid => {
				// 		if (valid) {				
				// 				this.$u.post("/worker/yujian",this.form).then(result=>{
				// 				this.result = result;	
								
				// 				this.isShow=true;
								
				// 				this.form={
				// 					yuyueId:'',
				// 					diya:'',
				// 					gaoya:'',
				// 					isFuyao:0,
				// 					tiwen:'',
				// 					yaowumingcheng:''
				// 				}
				// 				})
				// 		}
				// 	})
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
