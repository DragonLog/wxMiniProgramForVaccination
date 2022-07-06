<template>
	<view>
		<uni-list>
		    <view class="uni-list-cell" hover-class="uni-list-cell-hover" v-for="(value, key) in list" :key="key" style="margin-top: 10rpx;" @click="jump(value.id)">
		        <view class="uni-media-list">
		            <view class="uni-media-list-body">
		               <view class="uni-media-list-text-top">
							<text>接种计划：{{ value.id }}</text>
					   </view>
						<view class="uni-media-list-text-bottom">
						    <text>计划时间：{{value.startDate + ' 到 ' + value.endDate}}</text>
						</view>
						<view class="uni-media-list-text-bottom">
						    <text style="color: red;">余量：{{ value.amount }}</text>
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
				vaccineId:"",
				inoculateSiteId:""
	        };
	    },
	    onLoad(option) {
			this.vaccineId = option.vaccineId;
			this.inoculateSiteId = option.inoculateSiteId;
			this.init();
	    },
		onShow() {
			this.list.length = 0;
			this.init();
		},
	    methods: {
	        init(){
	        	this.$u.get('/plan/findPlans/' + this.vaccineId + '/' + this.inoculateSiteId).then(res=>{
	    		console.log(res);
				if (res.code==0) {
					this.list = res.data;
				}
	    	})
	    },
		jump(planId) {
			uni.navigateTo({
				url:"./appoint?planId=" + planId
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
            margin-right: 32rpx;
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
            margin-top: 20rpx;
            margin-right: 20rpx;
            font-size: 27rpx;
            color: #999999;
        }
    }
</style>
