<template>
	<view>
		<uni-list>
		    <view class="uni-list-cell" hover-class="uni-list-cell-hover" v-for="(value, key) in list" :key="key" style="margin-top: 10rpx;">
		        <view class="uni-media-list" style="border-bottom: 2rpx solid #eeeeee;">
					<view>
					    <text style="line-height: 250rpx; font-size: 30rpx;">医护人员：{{value.workRealName}}</text>
					</view>
		            <view class="uni-media-list-body">
						<view class="uni-media-list-text-top">
								<text>{{value.createTime}}</text>
						</view>
						<view class="uni-media-list-text-bottom">
						    <text>接种点：{{value.inoculateSiteName}}</text>
						</view>
						<view class="uni-media-list-text-bottom">
						    <text>所服药物名称：{{value.medicine}}</text>
						</view>
						<view class="uni-media-list-text-bottom">
						    <text>禁忌症名称：{{value.contraindication}}</text>
						</view>
						<view class="uni-media-list-text-bottom">
						    <text>低压:{{value.bloodPressureLow}}  高压:{{value.bloodPressureHigh}}  体温:{{value.temperature}}</text>
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
		onLoad() {
			this.init();
		},
		methods: {
			init(){
				this.$u.get("/user/findPreCheckHistories").then(res=>{
					console.log(res)
					if(res.code === 0){
						this.list=res.data;
					}
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
