<template>
<header class="navbar-expand-md">
    <div class="navbar-collapse" id="navbar-menu">
        <div class="navbar">
            <div class="container-lg">
                <ul class="navbar-nav m-nav-inline-box">
                    <li :class="['nav-item', isActive('/') ? 'active' : '']">
                        <router-link class="nav-link" to="/">
                            <span class="nav-link-icon d-lg-inline-block">
                                <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                                    <path stroke="none" d="M0 0h24v24H0z" fill="none" />
                                    <path d="M5 12l-2 0l9 -9l9 9l-2 0" />
                                    <path d="M5 12v7a2 2 0 0 0 2 2h10a2 2 0 0 0 2 -2v-7" />
                                    <path d="M9 21v-6a2 2 0 0 1 2 -2h2a2 2 0 0 1 2 2v6" /></svg>
                            </span>
                            <span class="nav-link-title">
                                Home
                            </span>
                        </router-link>
                    </li>
                    <li :class="['nav-item', isActive('/billing-invoice') ? 'active' : '']">
                        <router-link class="nav-link" to="/billing-invoice">
                            <span class="nav-link-icon d-lg-inline-block">
                                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon icon-tabler icons-tabler-outline icon-tabler-businessplan">
                                    <path stroke="none" d="M0 0h24v24H0z" fill="none" />
                                    <path d="M16 6m-5 0a5 3 0 1 0 10 0a5 3 0 1 0 -10 0" />
                                    <path d="M11 6v4c0 1.657 2.239 3 5 3s5 -1.343 5 -3v-4" />
                                    <path d="M11 10v4c0 1.657 2.239 3 5 3s5 -1.343 5 -3v-4" />
                                    <path d="M11 14v4c0 1.657 2.239 3 5 3s5 -1.343 5 -3v-4" />
                                    <path d="M7 9h-2.5a1.5 1.5 0 0 0 0 3h1a1.5 1.5 0 0 1 0 3h-2.5" />
                                    <path d="M5 15v1m0 -8v1" /></svg>
                            </span>
                            <span class="nav-link-title">
                                Billing Report
                            </span>
                        </router-link>
                    </li>
                    <li :class="['nav-item', isActive('/guide') ? 'active' : '']">
                        <router-link class="nav-link" to="/guide">
                                <span class="nav-link-icon d-lg-inline-block">
                                    <svg  xmlns="http://www.w3.org/2000/svg"  width="24"  height="24"  viewBox="0 0 24 24"  fill="none"  stroke="currentColor"  stroke-width="2"  stroke-linecap="round"  stroke-linejoin="round"  class="icon icon-tabler icons-tabler-outline icon-tabler-alarm">
                                    <path stroke="none" d="M0 0h24v24H0z" fill="none"/>
                                    <path d="M12 13m-7 0a7 7 0 1 0 14 0a7 7 0 1 0 -14 0" />
                                    <path d="M12 10l0 3l2 0" />
                                    <path d="M7 4l-2.75 2" />
                                    <path d="M17 4l2.75 2" /></svg>
                                </span>
                                <span class="nav-link-title">
                                    Alarm
                                </span>
                        </router-link>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</header>
</template>

    
<script>
export default {
    name: 'DashboardHeader',
    data() {
        return {};
    },
    methods: {
        isActive(path) {
            const route = this.$route;
            return route.path === path;
        }
    }
}
</script>

<style lang="scss">
.navbar{
  .m-nav-inline-box{
    display: -webkit-inline-box;
  }
}
</style>
