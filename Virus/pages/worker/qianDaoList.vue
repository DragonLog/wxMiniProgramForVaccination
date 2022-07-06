<template>
	<view class="page" >
		<u-cell-group >
			
			<u-cell-item 
			 v-for="item in list"
			:key="item.index"  
			:title="'签到对象:'+item.userRealName"  

			:value=item.createTime
			hover-class="none" 
			:arrow="false" 
			
			>
			
			</u-cell-item>

		</u-cell-group>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				// data:{
				// 	jiezhongbuwei:'',
				// 	jiezhongren:'',
				// 	yimiaoZhonglei:''

				// },
				
				list:[]
			}
		},
		onLoad(){
			this.init();
		},
		methods: {
			init(){
				this.$u.get("/worker/findSignHistories").then(res=>{
					
					// 	for(let i=0;i<res.data.length;i++){
							
					// 		this.$u.get("/admin/queryUser?id="+res.data[i].qiandaoren).then(result=>{

					// 			res.data[i].qiandaoren=result.data.realName;
								
					// 		})
					// 		//this.list=res.data;
					
							
					// 	}
						
						
						
						
						this.list=res.data;
				})
			}
			
			
			
		}
	}
</script>


<style>
	
.page{
		padding: 50rpx;
		
	}
</style>
