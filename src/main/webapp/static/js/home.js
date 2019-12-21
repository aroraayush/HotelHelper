let map;
let globalBounds;
function initMap() {
    const center = {lat: 37.7058152, lng: -122.4779747};
    map = new google.maps.Map(document.getElementById('hotels_map'), {
        center: center,
        zoomControl: true,
        panControl: false,
        zoom: 10
    });
    globalBounds = new google.maps.LatLngBounds();
}
(async () => {
    try {
        const response = await fetch('/hotels',{method :'post'});
        if(response){

            const myJson = await response.json();
            const hotelLatLngData = myJson.data;
            hotelLatLngData.forEach(hotel=> {

                const hotelPosition = new google.maps.LatLng(hotel.latitude,hotel.longitude);
                const marker = new google.maps.Marker({
                    position: hotelPosition,
                    title: hotel.name
                });
                marker.setMap(map);
                globalBounds.extend(hotelPosition);
            });
        }
    }
    catch (e) {
        console.error("Error fetching cities"+e)
    }
})();