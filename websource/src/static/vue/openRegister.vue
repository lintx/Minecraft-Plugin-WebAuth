<template>
    <form class="form-signin" @submit="register">
        <div class="text-center mb-4">
            <h1 class="cover-heading">注册</h1>
        </div>

        <div class="mb-4">
            <div class="form-label-group">
                <input v-model="username" type="text" id="register_username" class="form-control" placeholder="帐号" required autofocus>
                <label for="register_username">帐号/玩家名</label>
            </div>

            <div class="form-label-group">
                <input v-model="password" type="password" id="register_password" class="form-control" placeholder="密码" required>
                <label for="register_password">密码</label>
            </div>

            <div class="form-label-group">
                <input v-model="re_password" type="password" id="register_re_password" class="form-control" placeholder="重复密码" required>
                <label for="register_re_password">重复密码</label>
            </div>
        </div>

        <button class="btn btn-lg btn-primary btn-block" type="submit">立即注册</button>
        <router-link class="btn btn-secondary btn-block" to="/login">已有帐号，去登录</router-link>
    </form>
</template>

<script>
    import Vue from "vue/dist/vue"
    import Session from "../js/libs/session";

    export default {
        data(){
            return {
                username:"",
                password:"",
                re_password: ""
            }
        },
        methods:{
            register(event){
                event.preventDefault();
                let vm = this;
                if (this.password !== this.re_password){
                    Vue.$toast.error("两次密码输入不一致");
                    return;
                }
                Session.register({username:this.username,password: this.password},()=>{
                    vm.$router.push("/login");
                })
            }
        },
    }
</script>