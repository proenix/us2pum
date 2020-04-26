<template>
    <div class="memo-field flip-container" v-bind:class="{ flip: isFlipped, hidden: !isVisible }" v-on:click="reverse()">
        <div class="flipper" v-bind:style="this.classObject">
            <div class="front">
                <img :src="'./media/img/' + this.front" />
            </div>
            <div class="back">
                <img :src="'./media/img/' + this.back" />
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: 'MemoField',
    props: {
        id: Number,
        front: String,
        back: String,
        isFlipped: {
            type: Boolean,
            default: false,
        },
        isSelected: {
            type: Boolean,
            default: false,
        },
        isVisible: {
            type: Boolean,
            default: true,
        },
        symbol: String,
    },
    data: function() {
        return {
            key: this.id,
        }
    },
    computed: {
        elementSize: function() {
            let w = (Math.floor((window.innerWidth*.9 - 20)/4));
            let h = (Math.floor((window.innerHeight*.9 - 60)/4));
            if (h > w) {
                return w;
            }
            return h;
        },
        classObject: function() {
            return {
                width: this.elementSize + 'px',
                height: this.elementSize + 'px',
            }
        }
    },
    methods: {
        reverse: function() {
            this.$emit('clickedOnField', this.key);
        }
    }
}
</script>

<style scoped>
.memo-field {
    margin: 1%;
    display: inline-block;
    transition: 0.6s;
	transform-style: preserve-3d;
    perspective: 100%;;

    position: relative;
}
.memo-field img {
    width: 100%;
    height: 100%;
    border: 3px solid green;
}

/* entire container, keeps perspective */
.flip-container {
	perspective: 1000px;
}

/* flip the pane when hovered */
.flip-container.flip .flipper {
    transform: rotateY(180deg);
}

.front, .back {
	width: 100%;
	height: 100%;
}

/* flip speed goes here */
.flipper {
	transition: 0.6s;
	transform-style: preserve-3d;

	position: relative;
}

/* hide back of pane during swap */
.front, .back {
	backface-visibility: hidden;

	position: absolute;
	top: 0;
	left: 0;
}

/* front pane, placed above back */
.front {
	z-index: 2;
	/* for firefox 31 */
	transform: rotateY(0deg);
}

/* back, initially hidden pane */
.back {
	transform: rotateY(180deg);
}
.hidden {
    opacity: 0;
    -webkit-transition: opacity 1s ease-in-out;
}
</style>