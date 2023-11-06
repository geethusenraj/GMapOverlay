package com.example.gmapoverlay

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.View.MeasureSpec
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.GroundOverlay
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PatternItem
import com.google.android.gms.maps.model.PolygonOptions


class MainActivity : AppCompatActivity(),
    OnMapReadyCallback, GoogleMap.OnGroundOverlayClickListener {

    private var overlayImage: BitmapDescriptor? = null
    private var groundOverlay: GroundOverlay? = null

    companion object {
        private const val PATTERN_GAP_LENGTH_PX = 20
        private const val PATTERN_DASH_LENGTH_PX = 20
        private const val POLYLINE_STROKE_WIDTH_PX = 12

        private val DEFAULT_LOCATION = LatLng(9.96001, 76.3725)
        private val GAP: PatternItem = Gap(PATTERN_GAP_LENGTH_PX.toFloat())
        private val DASH: PatternItem = Dash(PATTERN_DASH_LENGTH_PX.toFloat())
        private val PATTERN_POLYGON_ALPHA = listOf(GAP, DASH)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        map ?: return

        // Register a listener to respond to clicks on GroundOverlays.
        map.mapType = GoogleMap.MAP_TYPE_SATELLITE
        map.setOnGroundOverlayClickListener(this)
        map.setContentDescription("Google Map with ground overlay.")

        //initial positioning the map
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                DEFAULT_LOCATION,
                17f
            )
        )

        addGroundOverlayOnMap(map)
        showCircleGeofenceOnMap(map)
        drawPolygonOnMap(map)

        //Used arrow icon as markers on map
        map.addMarker(
            MarkerOptions()
                .position(LatLng(9.96141, 76.37023))
                .icon(bitmapDescriptorFromVector(this@MainActivity, R.drawable.icon_arrow))
                .anchor(1.2f, 0.5f).rotation(230f).flat(true)
        )


        showCustomTextOnMap(map)

        /**
         * Set map bounds
         */
        setMapBounds(map)


        /**
         * Workout - method call to add arrow in map using polyline
         */
//        drawPolylineWithArrowEndCap(map)

    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap =
                Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    private fun setMapBounds(map: GoogleMap) {
        val bounds = LatLngBounds.Builder()
        bounds.include(LatLng(9.96001, 76.3725))
        bounds.include(LatLng(9.9638, 76.37236))
        bounds.include(LatLng(9.96369, 76.36869))
        bounds.include(LatLng(9.96184, 76.3687))
        bounds.include(LatLng(9.95999, 76.36888))
        bounds.include(LatLng(9.95998, 76.36957))
        bounds.include(LatLng(9.95999, 76.37054))


        map.setOnMapLoadedCallback {
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 0))
        }
    }


    // Add shapes like circle on map overlay.
    private fun drawPolygonOnMap(map: GoogleMap) {
        val polygon = map.addPolygon(
            PolygonOptions()
                .clickable(true)
                .add(
                    LatLng(9.96188, 76.37022),
                    LatLng(9.96141, 76.37023),
                    LatLng(9.96139, 76.36953),
                    LatLng(9.96159, 76.36951),
                    LatLng(9.96185, 76.3695)
                )
        )
        polygon.strokePattern = PATTERN_POLYGON_ALPHA
        polygon.strokeWidth = POLYLINE_STROKE_WIDTH_PX.toFloat()
        polygon.strokeColor = resources?.getColor(R.color.red_border, this.theme)!!
        polygon.fillColor = resources?.getColor(R.color.yellow_filled, this.theme)!!
    }



    // Add shapes like circle on map overlay with zIndex value.
    private fun showCircleGeofenceOnMap(map: GoogleMap) {
        val circleOptions = CircleOptions()
            .center(LatLng(9.96203, 76.37146)).clickable(true).zIndex(1f)
            .radius(50.0) // In meters

        val circle = map.addCircle(circleOptions)
        circle.strokePattern = PATTERN_POLYGON_ALPHA
        circle.strokeWidth = POLYLINE_STROKE_WIDTH_PX.toFloat()
        circle.strokeColor = resources?.getColor(R.color.red_border, this.theme)!!
        circle.fillColor = resources?.getColor(R.color.yellow_filled, this.theme)!!
    }



    private fun addGroundOverlayOnMap(map: GoogleMap) {
        /**
         * With proper South-West & East-North points possible to add location bounds with
         * 'positionFromBounds' option in GroundOverlay rather than using 'position'.
         */
//        val latLng = LatLngBounds(
//            LatLng(9.96001, 76.3725),
//            LatLng(9.9638, 76.37236)
//        )

        val drawable = ContextCompat.getDrawable(this@MainActivity, R.drawable.ss_map_s)!!

        Log.d(
            "BOUNDS",
            "${drawable.intrinsicWidth.toFloat()}, ${drawable.intrinsicHeight.toFloat()}"
        )
        overlayImage = BitmapDescriptorFactory.fromResource(R.drawable.ss_map_s)
        overlayImage?.let {
            groundOverlay = map.addGroundOverlay(
                GroundOverlayOptions()
                    .image(it).anchor(0.2f, 1f)
//                    .positionFromBounds(latLng)
                    .position(
                        LatLng(9.95999, 76.36888),
                        drawable.intrinsicWidth.toFloat() - 450,
                        drawable.intrinsicHeight.toFloat() - 450
                    )
            )
        }
    }



    private fun showCustomTextOnMap(map: GoogleMap) {
        val distanceMarkerLayout: ConstraintLayout =
            layoutInflater.inflate(R.layout.custom_marker_layout, null) as ConstraintLayout
        distanceMarkerLayout.isDrawingCacheEnabled = true
        distanceMarkerLayout.measure(
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )
        distanceMarkerLayout.layout(
            0,
            0,
            distanceMarkerLayout.measuredWidth,
            distanceMarkerLayout.measuredHeight
        )
        distanceMarkerLayout.buildDrawingCache(true)
        distanceMarkerLayout.findViewById<TextView>(R.id.tvMarker).text = "Sample Text"
        val flagBitmap: Bitmap = Bitmap.createBitmap(distanceMarkerLayout.drawingCache)
        distanceMarkerLayout.isDrawingCacheEnabled = false
        val flagBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(flagBitmap)

        map.addMarker(
            MarkerOptions()
                .position(LatLng(9.9608, 76.37052))
                .icon(flagBitmapDescriptor).anchor(0.0f, 0.0f)
        )
    }

    override fun onGroundOverlayClick(groundOverlay: GroundOverlay) {
    }


    /**
     * Workout - method defenition to add arrow in map using polyline
     */
//    private fun drawPolylineWithArrowEndCap(map: GoogleMap): Polyline {
//
//        val arrowColor = Color.RED // change this if you want another color (Color.BLUE)
//        val lineColor = Color.RED
//
//        val endCapIcon: BitmapDescriptor = getEndCapIcon(this@MainActivity, arrowColor)!!
//
//        // have googleMap create the line with the arrow endcap
//        // NOTE:  the API will rotate the arrow image in the direction of the line
//
//        return map.addPolyline(
//            PolylineOptions()
//                .geodesic(true)
//                .color(lineColor)
//                .width(12f)
//                .startCap(RoundCap())
//                .endCap(CustomCap(endCapIcon, 12f))
//                .jointType(JointType.ROUND)
//                .add(LatLng(9.96224, 76.37736),
//                    LatLng(9.96174, 76.37735),
//                    LatLng(9.96171, 76.37612),
//                    LatLng(9.96219, 76.37609)))
//    }
//
//    private fun getEndCapIcon(context: Context?, color: Int): BitmapDescriptor {
//
//        // mipmap icon - white arrow, pointing up, with point at center of image
//        // you will want to create:  mdpi=24x24, hdpi=36x36, xhdpi=48x48, xxhdpi=72x72, xxxhdpi=96x96
//        val drawable = ContextCompat.getDrawable(context!!, R.drawable.icon_arrow)
//
//        // set the bounds to the whole image (may not be necessary ...)
//        drawable!!.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
//
//        // overlay (multiply) your color over the white icon
//        drawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
//
//        // create a bitmap from the drawable
//        val bitmap = Bitmap.createBitmap(
//            drawable.intrinsicWidth,
//            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
//        )
//
//        // render the bitmap on a blank canvas
//        val canvas = Canvas(bitmap)
//        drawable.draw(canvas)
//
//        // create a BitmapDescriptor from the new bitmap
//        return BitmapDescriptorFactory.fromBitmap(bitmap)
//    }


}