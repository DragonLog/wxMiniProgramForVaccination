<template>
	<view class="page">
		<u-form label-width='150'>
						<u-form-item label="预约日期">
							<u-input type='select' v-model="form.appointDate" @click="show=true"/>
							<u-calendar v-model="show"  @change="setDate" :min-date="startDate" :max-date="endDate" ></u-calendar>
						</u-form-item>
						<u-form-item label="时间段">
							<radio-group style="display: flex; flex-direction: column; line-height: 70rpx;" @change="setTime">
								<label v-for="(item, index) in list" :key="index">
									<radio :value="item.value" :checked="index === current" />
									<text>{{item.label}}</text>
								</label>
							</radio-group>
						</u-form-item>
						<u-button :loading="isLoading" @click="yuYue" type="success">疫苗预约</u-button>
				</u-form>
				<u-toast ref="uToast" />
	</view>
</template>

<script>
	export default {
		data() {
			return {
				planId:"",
				startDate:"",
				endDate:"",
				show:false,
				isLoading:false,
				form:{
					appointDate:"",
					planId:"",
					timeSlot:0
				},
				list:[],
				current:0,
				yibu:0
			};
		},
		onLoad(option) {
			this.planId = option.planId;
			this.form.planId = option.planId;
			this.init();
		},
		methods:{
			init(){
				this.list.length = 0;
				this.$u.post('/plan/findPlan/', this.form).then(res=>{
				console.log(res);
				if (res.code==0) {
					this.startDate = res.data.plan.startDate;
					this.endDate = res.data.plan.endDate;
					if(this.yibu == 0) {
						this.form.appointDate = res.data.plan.endDate;
					}
					this.list.push({
						label:"上午 "+res.data.plan.startTimeMorning+"点 - "+res.data.plan.endTimeMorning+"点" + "   剩余：" + res.data.morningRemain,
						value:0
					});
					this.list.push({
						label:"下午 "+res.data.plan.startTimeAfternoon+"点 - "+res.data.plan.endTimeAfternoon+"点" + "   剩余：" + res.data.afternoonRemain,
						value:1
					});
					this.list.push({
						label:"晚上 "+res.data.plan.startTimeEvening+"点 - "+res.data.plan.endTimeEvening+"点" + "   剩余：" + res.data.eveningRemain,
						value:2
					});
				}
				this.yibu = 1;
			})
			},
			setDate(evt){
							this.form.appointDate = evt.result;
							this.init();
						},
			setTime(evt) {
				this.form.timeSlot = evt.detail.value;
			},
			yuYue(){
							this.isLoading=true;
							this.$u.post("/user/appoint/ok",this.form).then(res=>{
								if(res.code == 0){
									uni.navigateTo({
									 	url:"./pay?planId=" + this.planId + "&appointDate=" + this.form.appointDate + "&timeSlot=" + this.form.timeSlot
									 });
								}else{
									this.$refs.uToast.show({
										title :res.msg,
										type : 'error'
									})
								}
							}).finally(()=>{
								this.isLoading=false;
							})
						}
		}
	}
</script>

<style lang="scss">
 .page {
	 padding:30rpx;
 }
</style>
