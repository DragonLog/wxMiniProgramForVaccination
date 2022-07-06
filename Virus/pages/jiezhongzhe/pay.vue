<template>
	<view>
		<view>
			<image src="../../static/wxpay.jpg" style="width: 100%; height: 700rpx"></image>
		</view>
		<view>
			<text style="text-align: center; font-size: 40rpx; display: block; margin-top: 80rpx;">{{name}}预约费用共计</text>
		</view>
		<view>
			<text style="text-align: center; font-size: 50rpx; display: block; color: red; margin-top: 10rpx;">￥{{price}} 元</text>
		</view>
		<view>
			<button type="primary" style="width: 50%; margin-top: 100rpx;" @click="appoint">支付成功</button>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				planId:"",
				price:0,
				name:"",
				form:{
					userId:uni.getStorageSync("userInfo").user.id,
					planId:"",
					timeSlot:0,
					appointDate:""
				}
			};
		},
		onLoad(option) {
			this.planId = option.planId;
			this.form.planId = option.planId;
			this.form.timeSlot = option.timeSlot;
			this.form.appointDate = option.appointDate;
			this.init();
		},
		methods: {
			init(){
				this.$u.get("/plan/findVaccine/" + this.planId).then(res=>{
					if (res.code == 0) {
						this.price = res.data.price;
						this.name = res.data.name
					}
				})
			},
			appoint() {
				this.$u.post("/user/pay/save",this.form).then(res=>{
					if(res.code == 0){
						uni.showModal({
							  title: '预约成功',
							 content: '您已经预约成功！请您准时在预约的时间到达预约接种点完成预约',
							showCancel:false,
							  success(res) {
								if (res.confirm) {
									uni.reLaunch({
										url:"jieZhong"
									})
								}
							}
							})

					}else{
						this.$refs.uToast.show({
							title :res.msg,
							type : 'error'
						})
					}
				})
			}
		}
	}
</script>

<style lang="scss">

</style>
