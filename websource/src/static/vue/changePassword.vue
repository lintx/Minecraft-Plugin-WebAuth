<template>
    <form class="form-signin" @submit="changePassword">
        <div class="text-center mb-4">
            <h1 class="cover-heading">修改密码</h1>
        </div>

        <div class="mb-4">
            <div class="form-label-group">
                <input v-model="password" type="password" id="register_password" class="form-control" placeholder="新密码" required>
                <label for="register_password">新密码</label>
            </div>

            <div class="form-label-group">
                <input v-model="re_password" type="password" id="register_re_password" class="form-control" placeholder="重复密码" required>
                <label for="register_re_password">重复密码</label>
            </div>
        </div>

        <button class="btn btn-lg btn-primary btn-block" type="submit">修改密码</button>
    </form>
</template>

<script>
    import Vue from "vue/dist/vue"
    import Session from "../js/libs/session";

    export default {
        data(){
            return {
                password:"",
                re_password: ""
            }
        },
        methods:{
            changePassword(event){
                event.preventDefault();
                let vm = this;
                if (this.password !== this.re_password){
                    Vue.$toast.error("两次密码输入不一致");
                    return;
                }
                Session.changePassword(this.password,()=>{
                    vm.$router.push("/");
                });
            }
        },
    }
</script>