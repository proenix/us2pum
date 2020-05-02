<template>
    <div class="contact-item" v-on:click="openContact">
        <div class="col">
            <img :src="contactPhoto" class="contact-item-image" />
        </div>
        <div class="col">
            <span class="contact-item-last-name">{{ displayName }}</span>
            <br>
            <ul class="contact-item-phone-numbers">
                <li v-for="(phone) in phoneNumbers" :key="phone.number">
                    {{ phone }}
                </li>
            </ul>
        </div>
    </div>
</template>

<script>
import imagePlaceholder from './../../public/assets/blank.png'
export default {
    name: 'ContactItem',
    data: function() {
        return {
            imagePlaceholder: imagePlaceholder,
        }
    },
    computed: {
        contactPhoto: function() {
        if (this.photo != "") {
            return 'data:image/jpg;base64,' + this.photo;
        }
        return imagePlaceholder;
        }
    },
    props: {
        displayName: {
            type: String,
            default: 'Name of Contact',
        },
        phoneNumbers: {
            type: Object,
        },
        id: String,
        lookup_key: String,
        photo: {
            type: String,
            default: "",
        },
    },
    methods: {
        openContact: function() {
            this.$emit('contactClicked', this.id, this.lookup_key);
        }
    }

}
</script>

<style scoped>
.contact-item .col {
    display: inline-block;
    vertical-align: middle;
    max-width: 75%;
}
.contact-item-last-name {
    font-weight: 700;
}
.contact-item {
    margin: 0.4em;
    padding: 0.3em;
    background: #f9feff;
    border-radius: 10px;
}
.contact-item-phone-numbers {
    margin: 0;
    padding: 0;
}
.contact-item-phone-numbers li {
    list-style-type: none;
}
.contact-item-image {
    width: 60px;
    height: 60px;
    margin-right: 10px;
    border-radius: 20%;
}
</style>