package frdk.geom;

import processing.core.*;
import java.util.ArrayList;

//GeomManager?
/*
    useful functions:

    Shape creation
        standards for ellipses, rects, regular polygons
        stars? bezier curves?

    SVG loading!

    boolean functions: intersect, diff, union, xor
        returns new fshape

    intersection functions
        FShape/FShape
        FShape/line
        FShape/ray
    
*/

public class FG{

    static float epsilon = .0001f;  //accuracy range of calculations, tinker with for best results
    static PVector testDir = new PVector(1, 0);

    //returns true if line a-b intersects line c-d
    //intersection point stored in intPoint
    public static boolean lineToLine(PVector a, PVector b, PVector c, PVector d, PVector intPoint){
        PVector ab = new PVector(b.x - a.x, b.y - a.y);
        PVector cd = new PVector(d.x - c.x, d.y - c.y);
        
        //based on equation:
        //ab * t + a = cd * u + c , which leads to:
        //cd.y * (a.x - c.x) ) - (cd.x * (a.y - c.y) = t * ((ab.y * cd.x) - (ab.x * cd.y))

        float t = (cd.y * (a.x - c.x) ) - (cd.x * (a.y - c.y));  //if this == 0, lines are parallel
        if( PApplet.abs(t) > epsilon ) {
            t = t / ((ab.y * cd.x) - (ab.x * cd.y));  //complete t calculation
            //test if intersection point is in on line segment ab i.e. 0 < t < 1
            if(t > 0 && t < 1) {
                //now, calculate intersection point and set in PVector
                intPoint.set( (ab.x * t) + a.x, (ab.y * t) + a.y);
                //test if intersection point is on line segment cd
                //based on dot product of c->d and c->int
                //dot product must be positive and less than the sqrt of cd
                PVector c_int = new PVector(intPoint.x - c.x , intPoint.y - c.y );
                float dotProduct = PVector.dot(cd, c_int);
                if(dotProduct > 0 && dotProduct < cd.magSq()) {
                    return true;
                } 
                if(PVector.dist(intPoint, a) < epsilon || 
                PVector.dist(intPoint, b) < epsilon || 
                PVector.dist(intPoint, c) < epsilon || 
                PVector.dist(intPoint, d) < epsilon ){
                    //finally, to account for edges, this algorithm is slightly generous
                    return true;
                }
            }
        }
        return false;
    }

    //returns true if ray extending out from a-b intersects line c-d
    //intersection point stored in intPoint
    public static boolean rayToLine(PVector a, PVector b, PVector c, PVector d, PVector intPoint){  
        PVector ab = new PVector(b.x - a.x, b.y - a.y);  //ab is the ray
        PVector cd = new PVector(d.x - c.x, d.y - c.y);  //cd is the line
        
        //based on equation:
        //ab * t + a = cd * u + c , which leads to:
        //cd.y * (a.x - c.x) ) - (cd.x * (a.y - c.y) = t * ((ab.y * cd.x) - (ab.x * cd.y))
        
        float t = (cd.y * (a.x - c.x) ) - (cd.x * (a.y - c.y));  //if this == 0, lines are parallel
        if( PApplet.abs(t) > epsilon ) {
            t = t / ((ab.y * cd.x) - (ab.x * cd.y));  //complete t calculation
            //test if intersection point is in front of ray i.e. t > 0
            if(t > 0) {
                //now, calculate intersection point and set in PVector
                intPoint.set( (ab.x * t) + a.x, (ab.y * t) + a.y );
                
                //test if intersection point is on this line segment
                //based on dot product of c->d and c->int
                //dot product must be positive and less than the sqrt of cd
                PVector c_int = new PVector(intPoint.x - c.x , intPoint.y - c.y );
                float dotProduct = PVector.dot(cd, c_int);
                if(dotProduct > 0 && dotProduct < cd.magSq()) {
                    return true;
                } else if(PVector.dist(intPoint, c) < epsilon || 
                        PVector.dist(intPoint, d) < epsilon ){
                    //finally, to account for edges, this algorithm is slightly generous
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean pointInPoly(PVector point, FPolygon poly){
        PVector intPoint = new PVector();
        int count1 = 0;
        PVector[] verts = poly.getVerts();
        for (int i = 0; i < verts.length; i++) {
            PVector v1 = verts[i];
            PVector v2 = verts[ (i+1) % verts.length ];
            if( rayToLine(point, PVector.add(point, testDir), v1, v2, intPoint) ) {
                count1++;
            }
        }
        count1 = count1 % 2;
        return ( count1 > 0 );
    }

    public static PVector[] polyPolyIntersection(FPolygon p1, FPolygon p2){
        ArrayList<PVector> intPoints = new ArrayList<PVector>();

        PVector[] p1Verts = p1.getVerts();
        PVector[] p2Verts = p2.getVerts();

        for(int i = 0; i < p1Verts.length; i++){
            PVector a = p1Verts[i];
            PVector b = p1Verts[i % p1Verts.length];
            for(int j = 0; j < p2Verts.length; j++){
                PVector c = p2Verts[j];
                PVector d = p2Verts[j % p2Verts.length];
                PVector intPoint = new PVector();

                lineToLine(a,b,c,d,intPoint);

                intPoints.add(intPoint);
            }
        }
        PVector[] toReturn  = new PVector[intPoints.size()];
        toReturn = intPoints.toArray(toReturn);
        return toReturn;
    }
}