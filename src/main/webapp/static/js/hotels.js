const hotelsParent = document.getElementsByClassName('hotels_parent')[0];
hotelsArr.forEach(hotel=> {
    const divOuter = document.createElement("div");
    divOuter.className="row";

    const divInner1 = document.createElement("div");
    divInner1.className="col-md-5";
    divInner1.innerHTML=`<span style="font-weight: bold;font-size: larger;font-weight: bold">${hotel.name === null || hotel.name.length === 0 ? "N/A" : `<a role=button href="/hotel?id=${hotel.hotel_id}">${hotel.name}</a>`}</span><hr>`;

    const divInner2 = document.createElement("div");
    divInner2.className="col-md-2";
    divInner2.innerHTML=`<span style="font-weight: bold;font-size: larger;">${hotel.address === null || hotel.address.length === 0 ? "Review : N/A" : hotel.address}</span><hr>`;

    const divInner3 = document.createElement("div");
    divInner3.className="col-md-2";
    divInner3.innerHTML=`<span style="font-weight: bold;font-size: larger;">${hotel.avg_rating === null || hotel.avg_rating.length === 0 || hotel.avg_rating == 0 ? "N/A" : parseFloat(hotel.avg_rating).toFixed(2) + '  | <span class="glyphicon glyphicon glyphicon-user" aria-hidden="true"></span> ' + hotel.review_count}</span><hr>`;

    const divInner4 = document.createElement("div");
    divInner4.className="col-md-2";
    divInner4.innerHTML=`<a role=button href="/hotel?id=${hotel.hotel_id}">Show Details <span class="glyphicon glyphicon-link"></span></a><hr>`;

    const divInner5 = document.createElement("div");
    divInner5.className="col-md-1";
    divInner5.setAttribute("style","text-align:center");
    divInner5.innerHTML=`<a href="http://www.google.com/maps/place/${hotel.latitude},${hotel.longitude}" target="_blank"><span class="glyphicon glyphicon-new-window"></span></a><hr>`;

    divOuter.appendChild(divInner1);
    divOuter.appendChild(divInner2);
    divOuter.appendChild(divInner3);
    divOuter.appendChild(divInner4);
    divOuter.appendChild(divInner5);
    hotelsParent.appendChild(divOuter)
});