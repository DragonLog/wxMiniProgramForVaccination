<template>
	<view class="page">
		<!-- 这是接种者页面 -->
		<view v-if="userType == 'user'">
			<u-cell-group>
				<u-cell-item @click="goto('yuYue')" icon='clock' title="预约" label="疫苗预约"></u-cell-item>
				<u-cell-item @click="goto('jieZhong')" icon='pushpin' title="接种" label="出示二维码进行接种"></u-cell-item>
				<u-line/>
				<u-cell-item @click="goto('payList')" icon='list-dot' title="支付历史记录" label="查看支付历史"></u-cell-item>
				<u-cell-item @click="goto('yuYueList')" icon='list-dot' title="预约历史记录" label="查看预约历史"></u-cell-item>
				<u-cell-item @click="goto('preCheckList')" icon='list-dot' title="预检历史记录" label="查看预检历史"></u-cell-item>
				<u-cell-item @click="goto('jieZhongList')" icon='list-dot' title="接种历史记录" label="查看接种历史"></u-cell-item>
			</u-cell-group>
		</view>
			
		
		<!-- 这是医护工作者 -->
		<view v-else>
			<u-cell-group>
				<u-cell-item @click="goto2('qianDao')" icon='edit-pen-fill' title="签到" label="扫描接种者二维码对其进行签到登记"></u-cell-item>
				<u-cell-item @click="goto2('yuJian')" icon='calendar-fill' title="预检" label="扫描接种者二维码对其进行预检登记"></u-cell-item>
				<u-cell-item @click="goto2('jieZhong')" icon='pushpin' title="接种" label="扫描接种者二维码对其进行接种登记"></u-cell-item>
				<u-cell-item @click="goto2('liiuGuan')" icon='more-dot-fill' title="留观" label="扫描接种者二维码对其进行留观登记"></u-cell-item>
			</u-cell-group>
			
			<u-line/>
			
			<u-cell-group>
				<u-cell-item @click="goto2('qianDaoList')" icon='list-dot' title="签到历史" label="为接种者进行过的签到服务历史"></u-cell-item>
				<u-cell-item @click="goto2('yuJianList')" icon='list-dot' title="预检历史" label="为接种者进行过的预检服务历史"></u-cell-item>
				<u-cell-item @click="goto2('jieZhongList')" icon='list-dot' title="接种历史" label="为接种者进行过的接种服务历史"></u-cell-item>
				<u-cell-item @click="goto2('liuGuanList')" icon='list-dot' title="留观历史" label="为接种者进行过的留观服务历史"></u-cell-item>
			</u-cell-group>
			
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				userType:''
			}
		},
		onLoad(){
			this.init();
		},
		methods: {
			init(){
			
				this.userType=this.$store.state.userLogined.userType;
				console.log(this.userType)
			},
			goto(res){
				uni.navigateTo({
					url:'../jiezhongzhe/'+res,
				})
			},
			
			goto2(res){
				uni.navigateTo({
					url:'../worker/'+res,
				})
			}
			
		}
	}
</script>

<style scoped>
		
</style>
