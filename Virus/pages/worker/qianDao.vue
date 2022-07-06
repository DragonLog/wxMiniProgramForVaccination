<template>
	<view class="page">
		<u-button type="primary" @click="scanCode">扫码</u-button>
		<view v-show="isShow" class="aaa">
			<view v-if="result.code === 0">
				签到成功
			</view>
			<view v-else>
				<text style="text-align: center; display: block; color: red; margin-top: 20rpx;">{{result.msg}}</text>
			</view>
		</view>
			
	</view>
</template>

<script>
	export default {
		data() {
			return {
				result:{},
				isShow:false
			}
		},
		methods: {
			scanCode(){
				this.isShow=false;
				console.log("res:"+111)
				uni.scanCode({
					onlyFromCamera: false,
					scanType: ['qrCode'],
					success: res=>{
						console.log("res:"+222)
						const id=JSON.parse(res.result);
						this.$u.post("/worker/sign/save",{id}).then(result=>{
						this.result = result;	
						console.log(result)
						this.isShow=true;
						})
					}
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
