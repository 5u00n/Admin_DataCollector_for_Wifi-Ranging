package com.projectopel.admindatacollector.UI.Visualization;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.projectopel.admindatacollector.R;

public class CalculatorActivity extends AppCompatActivity {

    // Define the center points and radii of the circles
    private double[][] centers = {{10.0, 20.0}, {15.0, 25.0}, {20.0, 30.0}};
    private double[] radii = {5.0, 7.0, 10.0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        // Call the method to calculate the intersection point
        double[] intersection = calculateIntersection(centers, radii);

        // Print the result
        Log.d("Intersection Point", "x: " + intersection[0] + ", y: " + intersection[1]);
    }

    private double[] calculateIntersection(double[][] centers, double[] radii) {
        // Initialize the variables
        double x = 0.0;
        double y = 0.0;
        double weightSum = 0.0;

        // Loop through all possible pairs of circles
        for (int i = 0; i < centers.length - 1; i++) {
            for (int j = i + 1; j < centers.length; j++) {
                // Calculate the distance between the centers
                double distance = Math.sqrt(Math.pow(centers[j][0] - centers[i][0], 2) + Math.pow(centers[j][1] - centers[i][1], 2));

                // Check if the circles intersect
                if (distance <= radii[i] + radii[j]) {
                    // Calculate the weight of the intersection point
                    double weight = Math.pow(radii[i], 2) - Math.pow(radii[j], 2) + Math.pow(distance, 2);
                    weight /= 2 * distance;

                    // Calculate the intersection point
                    double[] midpoint = {(centers[i][0] + centers[j][0]) / 2, (centers[i][1] + centers[j][1]) / 2};
                    double[] direction = {(centers[j][0] - centers[i][0]) / distance, (centers[j][1] - centers[i][1]) / distance};
                    double[] intersection = {midpoint[0] + weight * direction[0], midpoint[1] + weight * direction[1]};

                    // Add the weighted intersection point to the center point
                    x += intersection[0] * (1 / radii[i]);
                    y += intersection[1] * (1 / radii[i]);
                    weightSum += (1 / radii[i]);
                }
            }
        }

        // Calculate the average center point of the intersections
        double[] center = {x / weightSum, y / weightSum};

        return center;
    }
}