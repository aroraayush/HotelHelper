let map;
let globalBounds;
const addWishListButton = document.getElementById('add_wishlist_hotel');
const removeWishListButton = document.getElementById('remove_wishlist_hotel');
const addReviewButton = document.getElementById('add_review_btn');
const reviewForm = document.getElementById('review-form');
const submitForm = document.getElementById('submit_review_btn');
const  reviewsParent = document.getElementById('reviews_parent');

document.getElementById('hotel_name').innerHTML = name;
document.getElementById('hotel_address').innerHTML = address;

const expedia_link_div = document.getElementById('expedia_link')
expedia_link_div.innerHTML = `<a target="_blank" href="https://www.expedia.com/San-Francisco-Hotels.${hotel_id}.Hotel-Information">
    Visit on Expedia <span class="glyphicon glyphicon-new-window"></span>
    </a>`;

addReviewButton.addEventListener('click',()=>{
    addReviewButton.style.display = 'none';
    reviewForm.style.display = 'block';
})

expedia_link_div.addEventListener('click',()=>{
    const data = `hotel_id=${hotel_id}&link=https://www.expedia.com/San-Francisco-Hotels.${hotel_id}.Hotel-Information`;
    const xhr = new XMLHttpRequest();

    xhr.withCredentials = true;
    xhr.addEventListener("readystatechange", function () {
        if (this.readyState === 4  && xhr.status=== 204) {

        }
    });
    xhr.open("POST", "/visited");
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    xhr.send(data);
})

submitForm.addEventListener('click',()=>{
    const method = document.getElementById('form-type').value;
    let reviewId;
    if(method==="put" || method==="delete")
        reviewId = submitForm.getAttribute('data-reviewid');

    const text = document.getElementById('review_text').value;
    const title = document.getElementById('review_title').value;
    const rating = document.getElementById('rating_select');
    const ratingVal = rating.options[rating.selectedIndex].value;

    const isRecommended = document.getElementById('is_recommended');
    const isRecommendedVal = isRecommended.options[isRecommended.selectedIndex].value
    try {
        const dt = new Date();
        const date = `${
            (dt.getMonth()+1).toString().padStart(2, '0')}/${
            dt.getDate().toString().padStart(2, '0')}/${
            dt.getFullYear().toString().padStart(4, '0')} ${
            dt.getHours().toString().padStart(2, '0')}:${
            dt.getMinutes().toString().padStart(2, '0')}:${
            dt.getSeconds().toString().padStart(2, '0')}`;

        const date2 = `${
            dt.getFullYear().toString().padStart(4, '0')}-${
            (dt.getMonth()+1).toString().padStart(2, '0')}-${
            dt.getDate().toString().padStart(2, '0')} ${
            dt.getHours().toString().padStart(2, '0')}:${
            dt.getMinutes().toString().padStart(2, '0')}:${
            dt.getSeconds().toString().padStart(2, '0')}`;


        let data = `hotel_id=${hotel_id}&rating=${ratingVal}&title=${title}&review_text=${text}&is_recommended=${isRecommendedVal}&date=${date2}`;
        if(method==="put"){
            data += "&id="+reviewId
        }
        if(method==="delete"){
            data = `id=${reviewId}`;
        }
        const xhr = new XMLHttpRequest();
        xhr.withCredentials = true;

        xhr.addEventListener("readystatechange", function () {
            if (this.readyState === 4 && xhr.status === 200) {
                const response = JSON.parse(this.responseText);
                const insert_id = response.insert_id;

                const div = document.createElement("div");
                div.innerHTML =`<hr><span style="font-weight: bold">${title === null || title.length === 0 ? "Title: N/A" : title}</span>
                                <br><span>${text === null || text.length === 0 ? "Review : N/A" : text}</span>
                                <br><span>Rating : ${ratingVal === null || ratingVal.length === 0 ? "Rating: N/A" : ratingVal}</span>
                                <br><span><strong>Recommended</strong> : ${isRecommendedVal === null || isRecommendedVal.length === 0 ? "N/A" : (isRecommendedVal == 1 ? "YES" : "NO")}</span>
                                <br><span>Created by : ${username}</span>
                                <hr>`;
                reviewsParent.appendChild(div)
                reviewForm.style.display = 'none';
                location.reload();
            }
            else if (this.readyState === 4 && xhr.status === 204) {
                location.reload()
            }
        });
        let url = "/reviews";
        if(method==="delete"){
            url += `?id=${reviewId}`;
        }

        xhr.open(method, url);
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        xhr.send(data);
    }
    catch (e) {
        console.error("Error fetching attractions "+e)
    }
})

addWishListButton.addEventListener('click',()=>{
    updateWishList(hotel_id,1);
})

removeWishListButton.addEventListener('click',()=>{
    updateWishList(hotel_id,0);
})


function initMap() {
    const center = {lat: parseFloat(lat), lng: parseFloat(lng)};
    map = new google.maps.Map(document.getElementById('hotel_map'), {
        center: center,
        zoomControl: true,
        panControl: false,
        zoom: 14
    });
    globalBounds = new google.maps.LatLngBounds();
    const hotelPosition = new google.maps.LatLng(parseFloat(lat),parseFloat(lng));
    const marker = new google.maps.Marker({
        position: hotelPosition
    });
    marker.setMap(map);
    globalBounds.extend(hotelPosition);
}
(async () => {
    try {
        const response = await fetch("/reviews?hotel_id="+hotel_id, {"method":"GET"});
        if(response){
            const myJson = await response.json();
            const reviewsParent = document.getElementsByClassName('reviews_parent')[0];
            if(myJson.hasOwnProperty('data')){
                const reviewsArr = myJson.data;
                if(reviewsArr.length){
                    reviewsArr.forEach(review=> {
                        const div = document.createElement("div");
                        div.setAttribute("id", "div_"+review.review_id)
                        div.innerHTML =`<hr><span style="font-weight: bold">${review.title === null || review.title.length === 0 ? "Title: N/A" : review.title}</span><span style="float: right"><strong>Rating :</strong> ${review.rating === null || review.rating.length === 0 ? "Rating: N/A" : review.rating}</span>
                                    <br><span>${review.review_text === null || review.review_text.length === 0 ? "Review : N/A" : review.review_text}</span>
                                    <br><strong>Recommended: </strong>${review.is_recommended == 1 ? "YES" : "NO"}<br><div class="row"><div class="col-md-8"><span><strong>Created by</strong> : ${review.username === null || review.username.length === 0 ? "User: N/A" : review.username}</span>
                                    <span></div>
                                    <div class="col-md-4" style=";margin-top: 10px;"><button id="like_hotel_${review.review_id}" onclick="updateLikeStatus(${review.review_id},1)" class="like_hotel_button"><span class="glyphicon glyphicon-ok" aria-hidden="true"></span> Like </button>
                                    <button id="unlike_hotel_${review.review_id}" onclick="updateLikeStatus(${review.review_id},0)" type="button" class="unlike_hotel ${review.review_id} btn btn-warning" style="display: none; float: right;"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span> Unlike</button>
                                    </div>
                                </div></div>`;
                        reviewsParent.appendChild(div)
                    });
                    showHideLikeUnlike();
                    getReviewStatus();
                }
            }
            else {
                reviewsParent.innerHTML = "No reviews found";
            }
        }
    }
    catch (e) {
        console.error("Error fetching reviews "+e)
    }
})();

function updateLikeStatus(review_id,status) {
    const data = `review_id=${review_id}&status=${status}&hotel_id=${hotel_id}`;
    const xhr = new XMLHttpRequest();

    xhr.withCredentials = true;
    xhr.addEventListener("readystatechange", function () {
        if (this.readyState === 4  && xhr.status=== 204) {
            const likeButton = document.getElementById("like_hotel_"+review_id);
            const unlikeButton = document.getElementById("unlike_hotel_"+review_id);
            if(status ==1){
                likeButton.style.display = 'none';
                unlikeButton.style.display = 'block';
            }
            else {
                unlikeButton.style.display = 'none';
                likeButton.style.display = 'block';
            }
        }
    });
    xhr.open("POST", "/likes");
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    xhr.send(data);
}

async function showHideLikeUnlike(){
    try {
        const response = await fetch(`/likes?hotel_id=${hotel_id}&my_likes=true`, {"method":"GET"});
        if(response){
            const myJson = await response.json();
            if(myJson.hasOwnProperty('data')){
                const likes = myJson.data;
                if(likes.length){
                    likes.forEach(like=>{
                        const likeButton = document.getElementById("like_hotel_"+like.review_id);
                        const unlikeButton = document.getElementById("unlike_hotel_"+like.review_id);
                        if(like.status ==1){
                            if(likeButton && unlikeButton){
                                likeButton.style.display = 'none';
                                unlikeButton.style.display = 'block';
                            }
                        }
                    })
                }
            }
        }
    }
    catch (e) {
        console.error("Error fetching reviews "+e)
    }
};
async function getReviewStatus() {
    try {
        const response = await fetch("/reviews?review_id=true&hotel_id="+hotel_id, {"method":"GET"});
        if(response){
            const myJson = await response.json();
            if(myJson.hasOwnProperty('data')){
                const reviewIdInt = myJson.data;
                addReviewButton.style.display = 'none';
                const myReviewDiv = document.getElementById("div_"+reviewIdInt);
                if(myReviewDiv==null){
                    // location.reload();
                }
                else{
                    const div = document.createElement("DIV");
                    div.setAttribute('style','text-align:right;margin-top:10px');
                    const btn = document.createElement("BUTTON");
                    btn.setAttribute("id", "edit_review_btn");
                    btn.setAttribute("class", "btn btn-primary "+reviewIdInt);
                    btn.innerHTML = 'Edit Review';
                    div.append(btn)
                    document.getElementById('form-type').value = "put";

                    const deleteReviewbtn = document.createElement("BUTTON");
                    deleteReviewbtn.setAttribute("id", "edit_review_btn");
                    deleteReviewbtn.setAttribute("class", "btn btn-primary "+reviewIdInt);
                    deleteReviewbtn.setAttribute("style", "margin-left:10px");
                    deleteReviewbtn.innerHTML = 'Delete Review';
                    div.append(deleteReviewbtn)
                    myReviewDiv.append(div)
                    document.getElementById('form-type').value = "put";

                    submitForm.setAttribute('data-reviewid', reviewIdInt);

                    btn.addEventListener('click',(e)=>{
                        deleteReviewbtn.style.display = 'none';
                        btn.style.display = 'none';
                        reviewForm.style.display = 'block';
                    })

                    deleteReviewbtn.addEventListener('click',(e)=>{
                        btn.style.display = 'none';
                        deleteReviewbtn.innerHTML = 'Deleting...';
                        document.getElementById('form-type').value = "delete";
                        submitForm.click()
                    })
                }
            }
            getLikes();
        }
    }
    catch (e) {
        console.error("Error fetching reviews "+e)
    }
};
async function getLikes() {
    try {
        const response = await fetch("/likes?hotel_id="+hotel_id, {"method":"GET"});
        if(response){
            const myJson = await response.json();
            if(myJson.hasOwnProperty('data')){
                const likes = myJson.data;
                if(likes.length){
                    likes.forEach(like=>{
                        const div = document.getElementById("div_"+like.review_id);
                        if(div){
                            const innerdiv = document.createElement("div");
                            innerdiv.class = "row"
                            innerdiv.innerHTML = `${like.count} user(s) found this useful`
                            innerdiv.setAttribute("style","color: #989855;")
                            div.append(innerdiv)
                        }
                    })
                }
            }
        }
    }
    catch (e) {
        console.error("Error fetching reviews "+e)
    }
}
(() => {
    try {
        const data = `city=${city}&lat=${lat}&lng=${lng}&radius=2`;
        const xhr = new XMLHttpRequest();
        xhr.withCredentials = true;
        xhr.addEventListener("readystatechange", function () {
            if (this.readyState === 4) {
                const response = JSON.parse(this.responseText);
                const attractionsArr = response.data;
                const attractionsParent = document.getElementsByClassName('attractions_parent')[0];
                const attractionsParentDiv = document.getElementsByClassName('attractions_parent_div')[0];
                if(attractionsArr && attractionsArr.length>0){
                    attractionsParentDiv.style.display= 'block' ;
                    attractionsArr.forEach(attraction=> {
                        const div = document.createElement("div");
                        div.innerHTML =`<span style="font-weight: bold">${attraction.name === null || attraction.name.length === 0 ? "N/A" : attraction.name}</span>
                                    <br><span>Address: ${attraction.address === null || attraction.address.length === 0 ? "N/A" : attraction.address}</span>
                                    <br><span>Rating : ${attraction.rating === null || attraction.rating.length === 0 ? "N/A" : attraction.rating}</span><hr>`;
                        attractionsParent.appendChild(div)
                    });
                }
            }
        });
        xhr.open("POST", "/attractions");
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        xhr.send(data);
    }
    catch (e) {
        console.error("Error fetching attractions "+e)
    }
})();

function updateWishList(hotel_id, updateStatus) {
    try {
        const data = `hotel_id=${hotel_id}&status=${updateStatus}`;
        const xhr = new XMLHttpRequest();
        xhr.withCredentials = true;
        xhr.addEventListener("readystatechange", function () {
            if (this.readyState === 4 && xhr.status===204) {
                if(updateStatus ===1){
                    addWishListButton.style.display = 'none';
                    removeWishListButton.style.display = 'block';
                }
                else{
                    addWishListButton.style.display = 'block';
                    removeWishListButton.style.display = 'none';
                }
            }
        });
        xhr.open("POST", "/wishlist");
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        xhr.send(data);
    }
    catch (e) {
        console.error("Error fetching attractions "+e)
    }
}

(async () => {
    try {
        const response = await fetch("/wishlist?hotel_id="+hotel_id, {"method":"GET"});
        if(response){
            const myJson = await response.json();
            if(myJson.hasOwnProperty('data')){
                addWishListButton.style.display = 'none';
                removeWishListButton.style.display = 'block';
            }
            else {
                addWishListButton.style.display = 'block';
                removeWishListButton.style.display = 'none';
            }
        }
    }
    catch (e) {
        console.error("Error fetching reviews "+e)
    }
})();