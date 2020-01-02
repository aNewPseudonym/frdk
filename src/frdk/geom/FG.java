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

    static final int NO_INTERSECTION = 0;
    static final int X_INTERSECTION = 1;    //at mid of ab and cd, most common

    static final int T1_INTERSECTION = 2;   //at start of ab
    static final int T2_INTERSECTION = 3;   //at start of cd

    static final int V_INTERSECTION = 4;   // at start of ab and cd

    static final int X_OVERLAP = 5;
    static final int T1_OVERLAP = 6;
    static final int T2_OVERLAP = 7;
    static final int V_OVERLAP = 8;

    static float epsilon = .0000f;  //accuracy range of calculations, tinker with for best results
    static PVector testDir = new PVector(1, 0);

    //calculation for boolean operations
    //NOTE: does not cover all possible intersection cases, just the ones I'm looking for
    private static int intersectionTest(PVector a, PVector b, PVector c, PVector d, PVector intPoint){
        PVector ab = new PVector(b.x - a.x, b.y - a.y);
        PVector cd = new PVector(d.x - c.x, d.y - c.y);
        
        //based on equation:
        //ab * t + a = cd * u + c , which leads to:
        //(cd.y * (a.x - c.x)) - (cd.x * (a.y - c.y)) = t * ((ab.y * cd.x) - (ab.x * cd.y))
        //(ab.y * (a.x - c.x)) - (ab.x * (a.y - c.y)) = u * ((ab.y * cd.x) - (ab.x * cd.y)) 

        float t = (cd.y * (a.x - c.x)) - (cd.x * (a.y - c.y));  //if this == 0, lines are parallel
        float u = (ab.y * (a.x - c.x)) - (ab.x * (a.y - c.y));

        // non-parallel cases:
        if( PApplet.abs(t) > 0 ) {
            t = t / ((ab.y * cd.x) - (ab.x * cd.y));  //complete t calculation
            u = u / ((ab.y * cd.x) - (ab.x * cd.y));  //complete u calculation

            intPoint.set( (ab.x * t) + a.x, (ab.y * t) + a.y);

            if(t > 0 && t < 1 && u > 0 && u < 1) {
                return X_INTERSECTION;
            }
            if(t == 0 && u > 0 && u < 1){
                return T1_INTERSECTION;
            }
            if(t > 0 && t < 1 && u == 0){
                return T2_INTERSECTION;
            }
            if(t == 0 && u == 0){
                return V_INTERSECTION;
            }
        }
        // parallel cases:
        else {
            PVector ac = new PVector(c.x - a.x, c.y - a.y);
            t = PVector.dot(ac, ab) / PVector.dot(ab, ab);
            PVector ca = new PVector(a.x - c.x, a.y - c.y);
            u = PVector.dot(ca, cd) / PVector.dot(cd, cd);
            //FINISH OVERLAP CASES HERE ^^^
        }
    //no intersection
    intPoint = null;
    return NO_INTERSECTION;
    }

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

    public static boolean isPointInPoly(PVector point, FPolygon poly){
        PVector intPoint = new PVector();
        int count1 = 0;
        PVector[] verts = poly.getBound().getVerts();
        for (int i = 0; i < verts.length; i++) {
            PVector v1 = verts[i];
            PVector v2 = verts[ (i+1) % verts.length ];
            if( rayToLine(point, PVector.add(point, testDir), v1, v2, intPoint) ) {
                count1++;
            }
        }
        for(int h = 0; h < poly.contourCount(); h++){
            verts = poly.getContour(h).getVerts();
            for (int i = 0; i < verts.length; i++) {
                PVector v1 = verts[i];
                PVector v2 = verts[ (i+1) % verts.length ];
                if( rayToLine(point, PVector.add(point, testDir), v1, v2, intPoint) ) {
                    count1++;
                }
            }
        }
        count1 = count1 % 2;
        return ( count1 > 0 );
    }

    //Should this be FShape/FShape intersection?
    public static ArrayList<PVector> findIntersectionPoints(FPolygon p1, FPolygon p2){
        ArrayList<PVector> intPoints = new ArrayList<PVector>();

        PVector a, b, c, d;
        
        PVector[] p1Bound = p1.getBound().getVerts();
        PVector[] p2Bound = p2.getBound().getVerts();

        //bound-bound check
        for(int i = 0; i < p1Bound.length; i++){
            a = p1Bound[i];
            b = p1Bound[(i+1) % p1Bound.length];
            for(int j = 0; j < p2Bound.length; j++){
                c = p2Bound[j];
                d = p2Bound[(j+1) % p2Bound.length];
                PVector intPoint = new PVector();

                if (lineToLine(a,b,c,d,intPoint)) {
                    intPoints.add(intPoint);
                }
            }
        }

        //contours-bound check
        PVector[] p1Contour;
        for(int h = 0; h < p1.contourCount(); h++){
            p1Contour = p1.getContour(h).getVerts();
            for(int i = 0; i < p1Contour.length; i++){
                a = p1Contour[i];
                b = p1Contour[(i+1) % p1Contour.length];
                for(int j = 0; j < p2Bound.length; j++){
                    c = p2Bound[j];
                    d = p2Bound[(j+1) % p2Bound.length];
                    PVector intPoint = new PVector();
    
                    if (lineToLine(a,b,c,d,intPoint)) {
                        intPoints.add(intPoint);
                    }
                }
            }
        }

        //bound-contours check
        PVector[] p2Contour;
        for(int h = 0; h < p2.contourCount(); h++){
            p2Contour = p2.getContour(h).getVerts();
            for(int i = 0; i < p2Contour.length; i++){
                a = p2Contour[i];
                b = p2Contour[(i+1) % p2Contour.length];
                for(int j = 0; j < p1Bound.length; j++){
                    c = p1Bound[j];
                    d = p1Bound[(j+1) % p1Bound.length];
                    PVector intPoint = new PVector();
    
                    if (lineToLine(a,b,c,d,intPoint)) {
                        intPoints.add(intPoint);
                    }
                }
            }
        }

        //contours-contours check
        for(int h = 0; h < p1.contourCount(); h++){
            p1Contour = p1.getContour(h).getVerts();
            for(int i = 0; i < p1Contour.length; i++){
                a = p1Contour[i];
                b = p1Contour[(i+1) % p1Contour.length];
                for(int j = 0; j < p2.contourCount(); j++){
                    p2Contour = p2.getContour(j).getVerts();
                    for(int k = 0; k < p2Contour.length; k++){
                        c = p2Contour[k];
                        d = p2Contour[(k+1) % p2Contour.length];
                        PVector intPoint = new PVector();
        
                        if (lineToLine(a,b,c,d,intPoint)) {
                            intPoints.add(intPoint);
                        }
                    }
                }
            }
        }

        return intPoints;
    }

    //Should this be FShape/FShape intersection?
    public static ArrayList<PVector> linePolyIntersection(PVector a, PVector b, FPolygon poly){
        ArrayList<PVector> intPoints = new ArrayList<PVector>();

        PVector c, d;
        
        PVector[] bound = poly.getBound().getVerts();

        //bound check
        for(int i = 0; i < bound.length; i++){
            c = bound[i];
            d = bound[(i+1) % bound.length];
            PVector intPoint = new PVector();

            if (lineToLine(a,b,c,d,intPoint)) {
                intPoints.add(intPoint);
            }
        }

        //contours check
        PVector[] contour;
        for(int i = 0; i < poly.contourCount(); i++){
            contour = poly.getContour(i).getVerts();
            for(int j = 0; j < contour.length; j++){
                c = contour[j];
                d = contour[(j+1) % contour.length];
                PVector intPoint = new PVector();

                if (lineToLine(a,b,c,d,intPoint)) {
                    intPoints.add(intPoint);
                }
            }
        }

        return intPoints;
    }

    //--- FOR BOOLEAN OPERATIONS ---

    private static Node initNodeChain(FPath path){
        int lowest = path.findLowest();
        Node startNode = new Node( path.getVertex(lowest) );

        Node currentVertex = startNode;
        for(int i = 1; i < path.vertCount(); i++){
            currentVertex.next = new Node(path.getVertex( (lowest + i) % path.vertCount() ));
            currentVertex.next.prev = currentVertex;
            currentVertex = currentVertex.next;
        }
        currentVertex.next = startNode;

        return startNode;
    }

    private static ArrayList<Node> initNodes(FPolygon poly){
        ArrayList<Node> nodes = new ArrayList<Node>();  //I can't think of a better var name...
        
        nodes.add(initNodeChain(poly.getBound()));

        for(int i = 0; i < poly.contourCount(); i++){
            nodes.add(initNodeChain(poly.getContour(i)));
        }

        return nodes;
    }

    private static void checkIntersections(Node a, Node b, Node objStart, Node c){
        Node d = c.next;
        do{
            PVector intPoint = new PVector();
            if(lineToLine(a.pos, b.pos, c.pos, d.pos, intPoint)){
                Node vSubj = new Node(intPoint);
                Node vObj = new Node(intPoint);
                vSubj.intersect(vObj);
                vObj.intersect(vSubj);
    
                a.next = vSubj;
                vSubj.prev = a;
                b.prev = vSubj;
                vSubj.next = b;
                
                c.next = vObj;
                vObj.prev = c;
                d.prev = vObj;
                vObj.next = d;

                checkIntersections(a, vSubj, objStart, d);
                checkIntersections(vSubj, b, objStart, d);

                return;
            }
            c = d;
            d = d.next;
        } while( c != objStart );
    }

    private static void initIntersections(ArrayList<Node> subjNodes, ArrayList<Node> objNodes){
        for(Node subjStart : subjNodes){
            Node a = subjStart;
            Node b = a.next;
            do{
                for(Node objStart : objNodes){
                    checkIntersections(a, b, objStart, objStart);
                }
                a = b;
                b = b.next;
            } while( a != subjStart );
        }
    }

    private static void labelNodes(ArrayList<Node> subjNodes, ArrayList<Node> objNodes){
        for(Node subjStart : subjNodes){
            Node currentNode = subjStart;
            do{
                // do something..
            } while(currentNode != subjStart);
        }
    }

    private static void drawNodes(ArrayList<Node> nodes, PApplet app){
        app.pushStyle();
        app.noStroke();
        for(Node n : nodes){
            app.fill(0xff314a8b);
            app.ellipse(n.pos.x, n.pos.y, 12, 12);
            Node temp = n.next;
            while(temp != n){
                if(temp.isIntersection){
                    app.fill(0xffD10E3C);
                } else {
                    app.fill(0xff58aed1);
                }
                app.ellipse(temp.pos.x, temp.pos.y, 12, 12);
                temp = temp.next;
            }
        }
        app.popStyle();
    }

    //Should this be FShape/FShape intersection?
    public static FPolygon booleanOp(FPolygon subj, FPolygon obj, PApplet app){
        FPolygon result = new FPolygon();

        ArrayList<Node> subjNodes = initNodes(subj);
        ArrayList<Node> objNodes = initNodes(obj);

        labelNodes(subjNodes, objNodes);

        initIntersections(subjNodes, objNodes);

        drawNodes(subjNodes, app);
        drawNodes(objNodes, app);

        result = or(subjNodes, objNodes);

        app.pushStyle();
        app.stroke(0xff49FF33);
        app.strokeWeight(6);
        app.noFill();
        result.draw(app);
        app.pushStyle();

        return result;
    }

    private static FPolygon or(ArrayList<Node> subjNodes, ArrayList<Node> objNodes){
        FPolygon result = null;

        Node boundStart = subjNodes.get(0);
        subjNodes.remove(0);
        Node current = boundStart;
        do{
            FPath path = null;
            path = processNode_or(current, path);
            if(path != null){
                if(result == null){
                    result = new FPolygon(path);
                } else {
                    result.addContour(path);
                }
            }

            // move on to next vertex
            current = current.next;
        } while (current != boundStart);

        for(Node contourStart : subjNodes){
            current = contourStart;
            do{
                FPath path = null;
                path = processNode_or(current, path);
                if(path != null){
                    result.addContour(path);
                }

                // move on to next vertex
                current = current.next;
            } while (current != contourStart);
        }

        for(Node contourStart : objNodes){
            current = contourStart;
            do{
                FPath path = null;
                path = processNode_or(current, path);
                if(path != null){
                    result.addContour(path);
                }

                // move on to next vertex
                current = current.next;
            } while (current != contourStart);
        }

        return result;
    }

    private static FPath processNode_or(Node v, FPath path){
        // process current vertex

        // move on to next vertex
        if(v.isIntersection){
            v = v.cross;
            v.processed = true;
        }
        v = v.next;

        if(!v.processed){
            path = processNode_or(v, path);
        }

        return path;
    }

}