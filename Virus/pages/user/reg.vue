<template>
	<view class="page">
		<u-form label-width="200rpx" class="reg">
			
			<u-form-item label="用户名">
				<u-input v-model="form.username" />
			</u-form-item>
			
			<u-form-item label="密码">
				<u-input type="password" v-model="form.password" />
			</u-form-item>
			
			<u-form-item label="姓名">
				<u-input v-model="form.realName" />
			</u-form-item>
			
			<u-form-item label="身份证">
				<u-input v-model="form.cardId" />
			</u-form-item>
			
			<u-form-item label="手机">
				<u-input v-model="form.phone" />
			</u-form-item>
			
			<u-form-item label="所属行政区域">
				<u-input v-model="form.area" type="select" @click="isShow=true" />
				<u-picker v-model="isShow" mode="region" @confirm="setArea"></u-picker>
			</u-form-item>
			
			<u-form-item label="详细地址">
				<u-input v-model="form.address" />
			</u-form-item>
			
			
			<u-button :loading="isLoading" @click="reg" type="success">注册</u-button>
		<!-- <view class="tips">
			<text @click="toLogin">返回</text>
		</view> -->
		</u-form>
		<u-button  @click="toLogin"  class="back">返回</u-button>
		<u-toast ref="uToast" />
	</view>
</template>

<script>
	export default {
		data() {
			return {
					
				form:{
					username:'',
					password:'',
					realName:'',
					cardId:'',
					phone:'',
					area:'',
					districtCode:'',
					address:''
		
				},
				isLoading:false,
				isShow:false
				// rules:{
				// 	username:[
				// 		{
				// 		required: true, 
				// 		message: '请输入用户名', 
				// 		// 可以单个或者同时写两个触发验证方式 
				// 		trigger: ['change','blur'],
						
				// 		},
				// 		{
				// 			min: 3,
				// 			message:'用户名至少需要3个字符',
				// 			trigger: ['change','blur'],
				// 		},
				// 		{
				// 			max: 18,
				// 			message:'用户名最多18个字符',
				// 			trigger: ['change','blur'],
				// 		}
				// 	],
				// 	password:[
				// 		{
				// 			min:6,
				// 			max:20,
				// 			message:'密码必须是6-20位',
				// 			trigger:'blur'
				// 		}
				// 	],
				// 	password2:[
				// 		{
				// 			min:6,
				// 			max:20,
				// 			message:'密码必须是6-20位',
				// 			trigger:'blur'
				// 		},
				// 		{
				// 			validator: (rule, value, callback) => {
				// 				// 上面有说，返回true表示校验通过，返回false表示不通过
				// 				// this.$u.test.mobile()就是返回true或者false的
				// 				return this.form.password==this.form.password2;
				// 			},
				// 			message: '两次密码不同',
				// 			// 触发器可以同时用blur和change
				// 			trigger: 'blur',
				// 		}
				// 	],
				// 	realName:[
				// 		{
				// 		required: true, 
				// 		message: '请输入姓名', 
				// 		// 可以单个或者同时写两个触发验证方式 
				// 		trigger: ['change','blur'],
						
				// 		},
				// 		{
				// 			min: 2,
				// 			message:'姓名至少需要2个字符',
				// 			trigger: ['change','blur'],
				// 		},
				// 		{
				// 			max: 10,
				// 			message:'姓名最多10个字符',
				// 			trigger: ['change','blur'],
				// 		}
				// 	],
				// 	card:[
						
				// 		{
				// 			pattern:/^[1-9]\d{5}(18|19|([23]\d))\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/,
				// 		message:'身份证格式有误',
				// 		trigger: ['change','blur'],
				// 		}
				// 	],
				// 	phone:[
				// 		{
				// 			pattern: /^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$/,
				// 			message:'手机号格式有误',
				// 			trigger: ['change','blur'],
				// 		}
				// 	],
				// 	address:[
				// 		{
				// 			required: true,
				// 			message: '请输入地址', 
				// 			// 可以单个或者同时写两个触发验证方式 
				// 			trigger: ['change','blur'],
				// 		}
				// 	]
					
				// }
			}
		},
			onReady() {
				// this.$refs.uForm.setRules(this.rules);
			},
		methods: {
			reg(){
				// this.$refs.uForm.validate(valid => {
				// 				if (valid) {
				// 					this.isLoading=true;
				// 					this.$u.post("/user/register",this.form).then((res)=>{
				// 						if(res.success){
				// 							uni.showModal({
				// 							    title: '注册成功',
				// 							    content: '您已经注册成功！点击确定将跳转到登录界面',
				// 								showCancel:false,
				// 							    success: function (res) {
				// 							        if (res.confirm) {
				// 										uni.reLaunch({
				// 											url:'login'
				// 										})
				// 							            console.log('用户点击确定');
				// 							        } else if (res.cancel) {
				// 							            console.log('用户点击取消');
				// 							        }
				// 							    }
				// 							});
				// 						}else{
				// 							uni.showToast({
				// 								title:res.message
				// 							})
				// 						}
				// 					}).finilly(()=>{
				// 					this.isLoading=false;	
				// 					})
				// 				} 
				// 			});
							this.isLoading=true;
							this.$u.post("/user/save", this.form).then(res=>{
								if(res.code == 0){
																uni.showModal({
																    title: '注册成功',
																    content: '您已经注册成功！点击确定将跳转到登录界面',
																	showCancel:false,
																    success: function (res) {
																        if (res.confirm) {
																			uni.reLaunch({
																				url:'login'
																			})
																            console.log('用户点击确定');
																        } else if (res.cancel) {
																            console.log('用户点击取消');
																        }
																    }
																});
								}else{
									this.$refs.uToast.show({
										title :res.msg,
										type : 'error'
									})
								}
							}).finally(()=>{
								this.isLoading=false;
							})
			},
			setArea(res){
				this.form.districtCode=res.area.value;
				this.form.area=res.province.label+res.city.label+res.area.label
			},
			toLogin(){
				uni.reLaunch({
					url:'login'
				})
			}
			
		}
	}
</script>
<style>
	page{
		
		background-image: linear-gradient(#ffffff,#c8c5b3 );
	}
</style>

<style scoped>
	.page{
		padding: 80rpx;
	}
	.tips{
		
		text-align: right;
		margin-top: 20rpx;
	}
	.tips text{
		color: blue;
		line-height: 3em;
	}
	.reg{
		margin-top:10rpx;
		margin-bottom:40rpx;
		margin-right:50rpx;
		margin-left:50rpx;
	}
	.back{
		color:#18B566;
		width: 500rpx;
	}
	

</style>
