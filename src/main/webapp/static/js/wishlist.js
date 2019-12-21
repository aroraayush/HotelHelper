if(hotelsArr.length > 0){

    const hotelsParent = document.getElementsByClassName('hotels_parent')[0];

    hotelsArr.forEach(hotel=> {
        const divOuter = document.createElement("div");
        divOuter.className="col-md-12";

        const divInner1 = document.createElement("div");
        divInner1.className="col-md-5";
        divInner1.innerHTML=`<span style="font-weight: bold;font-size: larger;">${hotel.name === null || hotel.name.length === 0 ? "Title: N/A" : hotel.name}</span>`;

        const divInner2 = document.createElement("div");
        divInner2.className="col-md-4";
        divInner2.innerHTML=`<span style="font-weight: bold;font-size: larger;">${hotel.date === null || hotel.date.length === 0 ? "Review : N/A" : hotel.date}</span>`;

        const divInner3 = document.createElement("div");
        divInner3.className="col-md-3";
        divInner3.innerHTML=`<a role=button target="_blank" href="/hotel?id=${hotel.hotel_id}">View Hotel <span class="glyphicon glyphicon-new-window"></span></a>`;

        divOuter.appendChild(divInner1);
        divOuter.appendChild(divInner2);
        divOuter.appendChild(divInner3);
        hotelsParent.appendChild(divOuter)
    });
}
else{
    const superParent = document.getElementsByClassName('super_parent')[0];
    superParent.innerHTML = "<br><br><br><h2 style='text-align: center'>No Data Found. <br></h2><h3 style='text-align: center'>Please add some hotels to your wish list and come back</h3>";
}