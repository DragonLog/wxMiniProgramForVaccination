<template>
	<view class="page">
		<view>
			<text style="text-align: center; font-size: 40rpx; display: block;">个人基本信息</text>
		</view>
		<view class="row" style="margin-top: 30rpx;">
			<view class="label">用户名:</view>
			<view class="content">{{userInfo.username}}</view>	
		</view>
		<view class="row">
			<view class="label">姓&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;名:</view>
			<view class="content">{{userInfo.realName}}</view>	
		</view>
		<view class="row">
			<view class="label">身份证:</view>
			<view class="content">{{userInfo.cardId}}</view>	
		</view>
		<view class="row" v-show="userType=='jiezhongzhe'">
			<view class="label" v-if="userType === 'worker'">所属接种点:</view>
			<view class="content" v-if="userType === 'worker'">{{inoculateSiteName}}</view>	
			<view class="label" v-if="userType === 'user'">地址:</view>
			<view class="content" v-if="userType === 'user'">{{userInfo.address}}</view>	
		</view>
		<view class="row">
			<view class="label">电&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;话:</view>
			<view class="content">{{userInfo.phone}}</view>	
		</view>
		<u-button  type="primary" @click="edit" class="logoutBtn">修改信息</u-button>
		<u-button  type="error" @click="logout" class="logoutBtn">退出登录</u-button>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				userInfo:{
					"username":"",
					"realName":"",
					"cardId":"",
					"address":"",
					"phone":"",
				},
				inoculateSiteName:"",
				userType:''
				
			}
		},
		onLoad() {
			this.init()
		},
		methods: {
			logout(){
				this.$store.commit('logout');
				uni.reLaunch({
					url:'login'
				})
				
			},
			init(){
				this.userType=this.$store.state.userLogined.userType;
				if(this.userType === "worker") {
					this.userInfo=this.$store.state.userLogined.worker;
				} else {
					this.userInfo=this.$store.state.userLogined.user;
				}
				this.inoculateSiteName=this.$store.state.userLogined.inoculateSiteName;
			},
			edit() {
				uni.navigateTo({
					url:'edit'
				})
			}
		}
	}
</script>

<style scoped>
		.page{
			padding: 50rpx;
		}
		.row{
			display: flex;
			line-height: 150rpx;
			margin-right:50rpx;
			margin-left:50rpx;
			border-bottom: 2rpx solid #eeeeee;
		}
		/* .row+.row{
			border-top: 2rpx solid #eeeeee;
		} */
		
		.label{
			width: 150rpx;
		}
		.content{
			flex: 1;
		}
		/* .content::before{
			content: '';
		} */
		/* .search {
		display:flex;
		justify-content: center;//水平居中
		margin-bottom: 100rpx; 
			} */
			.logoutBtn{
				margin-top: 30rpx;
				width: 500rpx;
			}
</style>
