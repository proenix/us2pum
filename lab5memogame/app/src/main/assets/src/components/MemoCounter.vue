<template>
    <div class="board-counter">
        <div class="left">Liczba ruchów: <span id="movesCounter">{{ movesCounter }}</span></div>
        <div class="right">Pozostałe pary {{ pairsLeft }} z {{ pairsTotal }}</div>
    </div>
</template>

<script>
export default {
    name: 'MemoCounter',
    props: {
        movesCounter: {
            type: Number,
            default: 0,
        },
        pairsLeft: {
            type: Number,
            default: 0,
        },
        pairsTotal: {
            type: Number,
            default: 0,
        },
    }
}
</script>

<style scoped>
.board-counter {
    height: 50px;
    margin: 8px;
}
.right, .left {
    display: block;
}
</style>