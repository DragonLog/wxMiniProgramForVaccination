<template>
    <view>
        <uni-list>
            <view class="uni-list-cell" hover-class="uni-list-cell-hover" v-for="(value, key) in list" :key="key" style="margin-top: 10rpx;" @click="jump(value.id)">
                <view class="uni-media-list">
                    <view>
                        <image class="uni-media-list-logo" :src="'http://localhost:8080/vaccineImage/'+value.imgUrl"></image>
                    </view>
                    <view class="uni-media-list-body">
                       <view class="uni-media-list-text-top">
							<text>疫苗名称：{{ value.name }}</text>
					   </view>
					   <view class="uni-media-list-text-bottom">
					       <text style="color: red;">单价￥：{{ value.price }}</text>
					   </view>
						<view class="uni-media-list-text-bottom">
						    <text>生产厂家：{{ value.manufacturer }}</text>
						</view>
						<view class="uni-media-list-text-bottom">
						    <text>说明：{{ value.detail }}</text>
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
            };
        },
        onLoad() {
			this.init();
        },
        methods: {
            init(){
            	this.$u.get('/vaccine/findAll').then(res=>{
        		console.log(res)
        		if(res.code == 0){
        			this.list = res.data;
        		}
        	})
        },
			jump(target) {
				console.log("触发了" + target);
				uni.navigateTo({
					url:"./inoculateSite?vaccineId=" + target
				})
			}
        }
    };
</script>
<style lang="scss">
    .uni-media-list{
        display: flex;
        flex-direction: row;
        margin-left: 32rpx;
        margin-right: 32rpx;
        margin-top: 20rpx;
        
        .uni-media-list-logo {
            width: 170rpx;
            height: 170rpx;
        }
        
        
        .uni-media-list-body {
            flex-direction: row;
            height: auto;
            margin-left: 32rpx;
            margin-right: 32rpx;
            justify-content: space-around;
        }
         
        .uni-media-list-text-top {
            height: 40rpx;
            font-size: 35rpx;
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
