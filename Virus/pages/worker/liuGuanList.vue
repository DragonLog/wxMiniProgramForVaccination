<template>
	<view>
		<uni-list>
		    <view class="uni-list-cell" hover-class="uni-list-cell-hover" v-for="(value, key) in list" :key="key" style="margin-top: 10rpx;">
		        <view class="uni-media-list" style="border-bottom: 2rpx solid #eeeeee;">
					<view>
					    <text style="line-height: 160rpx; font-size: 30rpx;">留观对象：{{value.userRealName}}</text>
					</view>
		            <view class="uni-media-list-body">
						<view class="uni-media-list-text-top">
								<text>{{value.createTime}}</text>
						</view>
						<view class="uni-media-list-text-bottom">
						    <text>留观状态：{{value.status}}</text>
						</view>
						<view class="uni-media-list-text-bottom">
						    <text style="font-size: 25rpx;">结束时间：{{value.endTime}}</text>
						</view>
						<view class="uni-media-list-text-bottom">
						    <text style="color: red;">备注：{{value.note}}</text>
						</view>
		            </view>
		        </view>
		    </view>
		</uni-list>
	</view>
</template>

<script>
	export default {
		data() {
			return {			
				list:[]
			}
		},
		onLoad(){
			this.init();
		},
		methods: {
			init(){
				this.$u.get("/worker/findObserveHistories").then(res=>{
						this.list=res.data;
						console.log(this.list);
	
				})
			}
	
		}
	}
</script>

<style lang="scss">
.uni-media-list{
        display: flex;
        flex-direction: row;
        margin-left: 32rpx;
        margin-right: 32rpx;
        margin-top: 20rpx;
        
        .uni-media-list-logo {
            width: 180rpx;
            height: 180rpx;
        }
        
        
        .uni-media-list-body {
            flex-direction: row;
            height: auto;
            margin-left: 32rpx;
            margin-right: 5rpx;
            justify-content: space-around;
        }
         
        .uni-media-list-text-top {
            height: 40rpx;
            font-size: 30rpx;
            overflow: hidden;
        }
         
        .uni-media-list-text-bottom {
            display: flex;
            flex-direction: row;
            margin-top: 10rpx;
            margin-right: 20rpx;
            font-size: 27rpx;
            color: #999999;
        }
    }
</style>
