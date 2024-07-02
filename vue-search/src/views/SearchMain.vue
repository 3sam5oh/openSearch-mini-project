<template>
    <div class="wrap">
        <header class="header">
            <div class="headerName">Open<br>Search</div>
            <div class="search-container">
                <input type="search" placeholder="검색어 입력" v-model="searchQuery" @input="fetchSearchResults">
            </div>
        </header>
        <div class="content">
            <div class="context">
                <!-- 검색어 : {{ searchQuery }} -->
                <div v-if="results.length">
                    <!-- <p>검색 결과 :</p> -->
                    <ul>
                        <li v-for="result in results" :key="result.link">{{ result.title }}</li>
                    </ul>
                </div>
                <div v-else style="background-color: #fff; padding: 15px; border: none; border-radius: 10px;">
                    <p>검색결과가 없습니다.</p>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import '@/assets/css/index.css'
import axios from 'axios';

export default {
    name: "SearchMain",
    data() {
        return {
            searchQuery: '',
            results: []
        };
    },
    methods: {
        async fetchSearchResults() {
            const query = this.searchQuery.trim();
            if (query.length > 0) {
                try {
                    const response = await axios.get(`http://localhost:8080/api/aws/${query}`);
                    this.results = response.data;
                } catch (error) {
                    console.error('Error fetching search results:', error);
                    this.results = [];
                }
            } else {
                this.results = [];
            }
            // console.log(this.results);
        }
    }
};
</script>

