import "bootstrap"
import Vue from "vue/dist/vue"
import "../css/index.scss"
import Session from "./libs/session";
import VueRouter from "vue-router";
import VueToast from "vue-toast-notification";
import loginPage from "../vue/login.vue";
import indexPage from "../vue/index.vue";
import registerPage from "../vue/register.vue";
import helpPage from "../vue/help.vue";
import mePage from "../vue/me.vue";
import changePasswordPage from "../vue/changePassword.vue";
import changePlayerNamePage from "../vue/changePlayerName.vue";

Vue.use(VueRouter);
Vue.use(VueToast,{
    position:"top"
});

const routes = [
    {path:"/",name:"index",component:mePage},
    {path:"/login",name:"login",component:loginPage},
    {path:"/register",name:"register",component:registerPage},
    {path:"/help",name:"help",component:helpPage},
    {path:"/changePassword",name:"changePassword",component:changePasswordPage},
    {path:"/changeUserName",name:"changeUserName",component:changePlayerNamePage}
];

const router = new VueRouter({
    mode:'hash',
    routes: routes
});

var baseData = null;

router.beforeEach((to,from,next)=>{
    let hasRouter = false;
    routes.forEach((r)=>{
        if (r.path===to.path){
            hasRouter = true;
        }
    });
    if (!hasRouter){
        next("/");
        return;
    }
    if (baseData===null){
        Session.getBaseData((data)=>{
            baseData = data;
            routerBefore(to,from,next);
        });
    }else {
        routerBefore(to,from,next);
    }
});

function routerBefore(to,from,next){
    if (to.path!=="/login" && to.path!=="/register" && to.path!=="/help"){
        Session.getBaseData((data)=>{
            if (Session.getPlayerData().userId===0){
                next("/login");
            }else {
                next();
            }
        });
        return;
    }
    if (to.path==="/login" || to.path==="/register"){
        if (Session.getPlayerData().userId!==0){
            next("/");
            return;
        }
    }
    next();
}

let app = new Vue({
    el:"#app",
    router:router,
    render:c=>c(indexPage)
});
