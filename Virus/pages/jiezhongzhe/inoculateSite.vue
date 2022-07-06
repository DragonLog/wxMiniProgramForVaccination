<template>
	<view>
		<uni-list>
		    <view class="uni-list-cell" hover-class="uni-list-cell-hover" v-for="(value, key) in list" :key="key" style="margin-top: 10rpx;" @click="jump(vaccineId, value.id)">
		        <view class="uni-media-list">
		            <view>
		                <image class="uni-media-list-logo" :src="'http://localhost:8080/inoculateSiteImage/'+value.imgUrl"></image>
		            </view>
		            <view class="uni-media-list-body">
		               <view class="uni-media-list-text-top">
							<text>接种点：{{ value.name }}</text>
					   </view>
						<view class="uni-media-list-text-bottom">
						    <text>行政区域：{{value.province+value.city+value.district}}</text>
						</view>
						<view class="uni-media-list-text-bottom">
						    <text style="color: red;">地址：{{ value.address }}</text>
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
	            list:[],
				vaccineId:""
	        };
	    },
	    onLoad(option) {
			this.vaccineId = option.vaccineId;
			this.init();
	    },
	    methods: {
	        init(){
	        	this.$u.get('/plan/findInoculateSites/' + this.vaccineId).then(res=>{
	    		console.log(res);
				if (res.code==0) {
					this.list = res.data;
				}
	    	})
	    },
			jump(vaccineId, inoculateSiteId) {
				uni.navigateTo({
					url:"./plan?vaccineId="+vaccineId+"&inoculateSiteId="+inoculateSiteId
				})
			}
	    }
	}
</script>

<style lang="scss">
.uni-media-list{
        display: flex;
        flex-direction: row;
        margin-left: 5rpx;
        margin-right: 5rpx;
        margin-top: 20rpx;
        
        .uni-media-list-logo {
            width: 140rpx;
            height: 140rpx;
        }
        
        
        .uni-media-list-body {
            flex-direction: row;
            height: auto;
            margin-left: 22rpx;
            margin-right: 22rpx;
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
