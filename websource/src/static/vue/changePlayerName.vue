<script>
    import openChangePlayerNamePage from "./openChangePlayerName.vue";
    import closeChangePlayerNamePage from "./closeChangePlayerName.vue";
    import Session from "../js/libs/session";

    export default {
        data(){
            return {
                canChangePlayername:true
            }
        },
        render(createElement, context) {
            if (this.canChangePlayername){
                return createElement(openChangePlayerNamePage);
            }else {
                return createElement(closeChangePlayerNamePage);
            }
        },
        beforeCreate() {
            this.$nextTick(function () {
                let vm = this;
                Session.getBaseData((data) => {
                    vm.canChangePlayername = data.canChangePlayername;
                });
            });
        }
    }
</script>