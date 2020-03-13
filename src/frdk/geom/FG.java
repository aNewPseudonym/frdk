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

    //--- TRACE TYPES ---//
    public static final int AND = 0;
    public static final int OR = 1;
    public static final int NOT = 2;
    public static final int XOR = 3;

    //--- INTERSECTION FLAGS ---//
    static final int NO_INTERSECTION = 0;
    // non-parallel cases
    static final int X_INTERSECTION = 1;    //at mid of ab and cd, most common
    static final int T1_INTERSECTION = 2;   //at start of ab
    static final int T2_INTERSECTION = 3;   //at start of cd
    static final int V_INTERSECTION = 4;    //at start of ab and cd
    // colinear cases
    static final int X_OVERLAP = 5;         //both start on line
    static final int T1_OVERLAP = 6;        //a on cd
    static final int T2_OVERLAP = 7;        //c on ab
    static final int V_OVERLAP = 8;         //a and c share start point

    //--- SIDE FLAGS ---//
    static final int ON = 0;
    static final int LEFT = 1;
    static final int RIGHT = 2;

    static float epsilon = .0000f;  //accuracy range of calculations, tinker with for best results
    static PVector testDir = new PVector(1.0f, 0.1f);

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
                } 
                // else if(PVector.dist(intPoint, c) < epsilon || PVector.dist(intPoint, d) < epsilon ){
                //     //finally, to account for edges, this algorithm is slightly generous
                //     return true;
                // }
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

    //Should this be FShape/FShape intersection? Eventually...
    public static FPolygon booleanOp_debug(FPolygon subj, FPolygon obj, int traceType, PApplet app){
        FPolygon result = new FPolygon();

        //0 - Initialize Node Chain
        ArrayList<Node> subjNodes = initNodes(subj);
        ArrayList<Node> objNodes = initNodes(obj);

        //1 - Intersection Phase
        initIntersections(subjNodes, objNodes);

        //2 - Labeling Phase
        labelNodes_crossings(subjNodes, objNodes);
        labelNodes_crossings(objNodes, subjNodes);

        labelNodes_entries(subjNodes, obj);
        labelNodes_entries(objNodes, subj);

        //3 - Tracing Phase
        switch(traceType){
            case AND:
                result = trace_and(subjNodes, objNodes, obj, subj);
                break;
            case OR:
                result = trace_or(subjNodes, objNodes, obj, subj);
                break;
            case NOT:
                result = trace_not(subjNodes, objNodes, obj, subj);
                break;
            case XOR:
                result = trace_or(subjNodes, objNodes, obj, subj);
                break;
        }

        drawNodes(subjNodes, app, 2.0f);
        drawNodes(objNodes, app, 1.0f);

        return result;
    }

    private static ArrayList<Node> initNodes(FPolygon poly){
        ArrayList<Node> nodes = new ArrayList<Node>();
        
        nodes.add(initNodeChain(poly.getBound()));

        for(int i = 0; i < poly.contourCount(); i++){
            nodes.add(initNodeChain(poly.getContour(i)));
        }

        return nodes;
    }

    private static Node initNodeChain(FPath path){
        int lowest = path.findLowest();
        Node startNode = new Node( path.getVertex(lowest) );
        startNode.next = startNode;
        startNode.prev = startNode;

        Node currentNode = startNode;
        for(int i = 1; i < path.vertCount(); i++){
            Node newNode = new Node( path.getVertex( (lowest + i) % path.vertCount() ) );
            newNode.next = currentNode.next;
            newNode.next.prev = newNode;

            currentNode.next = newNode;
            newNode.prev = currentNode;
            currentNode = currentNode.next;
        }
        currentNode.next = startNode;

        return startNode;
    }

    private static void initIntersections(ArrayList<Node> subjNodes, ArrayList<Node> objNodes){
        for(Node subjStart : subjNodes){
            Node a = subjStart;
            Node b = a.next;
            do{
                for(Node objStart : objNodes){
                    b = a.next;
                    //test this segment against this object Node chain
                    checkIntersections(a, b, objStart, objStart);
                }
                // increment segment
                a = a.next;
                b = a.next;
            } while( a != subjStart );
        }
    }

    private static void checkIntersections(Node a, Node b, Node objStart, Node c){
        Node d = c.next;
        do{
            PVector intPoint = new PVector();
            int type = intersectionTest(a.pos, b.pos, c.pos, d.pos, intPoint);

            Node subjInt, objInt;

            switch(type){
                case NO_INTERSECTION:
                    break;
                case X_INTERSECTION:
                    subjInt = new Node(intPoint);
                    a.next = subjInt;
                    subjInt.prev = a;
                    b.prev = subjInt;
                    subjInt.next = b;

                    objInt = new Node(intPoint);
                    c.next = objInt;
                    objInt.prev = c;
                    d.prev = objInt;
                    objInt.next = d;

                    subjInt.cross = objInt;
                    objInt.cross = subjInt;

                    // test new subject segments with remaining object segments
                    checkIntersections(a, subjInt, objStart, d);
                    checkIntersections(subjInt, b, objStart, d);
                    return;
                case T1_INTERSECTION:
                    objInt = new Node(a.pos);
                    c.next = objInt;
                    objInt.prev = c;
                    d.prev = objInt;
                    objInt.next = d;

                    objInt.cross = a;
                    a.cross = objInt;

                    break;
                case T2_INTERSECTION:
                    subjInt = new Node(c.pos);
                    a.next = subjInt;
                    subjInt.prev = a;
                    b.prev = subjInt;
                    subjInt.next = b;

                    subjInt.cross = c;
                    c.cross = subjInt;

                    // test new subject segments with remaining object segments
                    checkIntersections(a, subjInt, objStart, d);
                    checkIntersections(subjInt, b, objStart, d);
                    return;
                case V_INTERSECTION:
                    a.cross = c;
                    c.cross = a;

                    break;
                case X_OVERLAP:
                    subjInt = new Node(c.pos);
                    objInt = new Node(a.pos);

                    a.next = subjInt;
                    subjInt.prev = a;
                    b.prev = subjInt;
                    subjInt.next = b;

                    c.next = objInt;
                    objInt.prev = c;
                    d.prev = objInt;
                    objInt.next = d;

                    subjInt.cross = c;
                    c.cross = subjInt;

                    objInt.cross = a;
                    a.cross = objInt;

                    // test new subject segments with remaining object segments
                    checkIntersections(a, subjInt, objStart, d);
                    checkIntersections(subjInt, b, objStart, d);
                    return;
                case T1_OVERLAP:
                    objInt = new Node(a.pos);

                    c.next = objInt;
                    objInt.prev = c;
                    d.prev = objInt;
                    objInt.next = d;

                    objInt.cross = a;
                    a.cross = objInt;

                    break;
                case T2_OVERLAP:
                    subjInt = new Node(c.pos);

                    a.next = subjInt;
                    subjInt.prev = a;
                    b.prev = subjInt;
                    subjInt.next = b;

                    subjInt.cross = c;
                    c.cross = subjInt;

                    // test new subject segments with remaining object segments
                    checkIntersections(a, subjInt, objStart, d);
                    checkIntersections(subjInt, b, objStart, d);
                    return;
                case V_OVERLAP:
                    a.cross = c;
                    c.cross = a;

                    break;
            }
            c = d;
            d = d.next;
        } while( c != objStart );
    }

    //NOTE: does not cover all possible intersection cases, just the ones relevant to the boolean op algorithm
    private static int intersectionTest(PVector a, PVector b, PVector c, PVector d, PVector intPoint){
        PVector ab = new PVector(b.x - a.x, b.y - a.y);
        PVector cd = new PVector(d.x - c.x, d.y - c.y);
        
        //based on equation:
        //ab * t + a = cd * u + c , which leads to:
        //(cd.y * (a.x - c.x)) - (cd.x * (a.y - c.y)) = t * ((ab.y * cd.x) - (ab.x * cd.y))
        //(ab.y * (a.x - c.x)) - (ab.x * (a.y - c.y)) = u * ((ab.y * cd.x) - (ab.x * cd.y)) 

        // 2nd half of above calculation
        // tests slopes: parallel if this equals 0
        float t = (ab.y * cd.x) - (ab.x * cd.y);
        float u = (ab.y * cd.x) - (ab.x * cd.y);

        // non-parallel cases:
        if( PApplet.abs(t) > 0 ) {
            t = ((cd.y * (a.x - c.x)) - (cd.x * (a.y - c.y))) / t;  //complete t calculation
            u = ((ab.y * (a.x - c.x)) - (ab.x * (a.y - c.y))) / u;  //complete u calculation

            if(t > 0 && t < 1 && u > 0 && u < 1) {
                intPoint.set((ab.x * t) + a.x, (ab.y * t) + a.y);
                return X_INTERSECTION;
            }
            else if(t == 0 && u > 0 && u < 1){
                return T1_INTERSECTION;
            }
            else if(t > 0 && t < 1 && u == 0){
                return T2_INTERSECTION;
            }
            else if(t == 0 && u == 0){
                return V_INTERSECTION;
            }
        }
        else {
            //test for colinearity, using triangle area calculation
            if(colinearTest(a, b, c) == 0 && (colinearTest(a, b, d) == 0)){
                // parallel cases:
                // t and u now reflect the position of a and c relative to each other
                if(ab.x != 0){
                    t = (c.x - a.x)/ab.x;
                } else {
                    t = (c.y - a.y)/ab.y;
                }
                if(cd.x != 0){
                    u = (a.x - c.x)/cd.x;
                } else {
                    u = (a.y - c.y)/cd.y;
                }

                if(t > 0 && t < 1 && u > 0 && u < 1){
                    return X_OVERLAP;
                }
                else if((t < 0 || t >= 1) && u > 0 && u < 1){
                    return T1_OVERLAP;
                }
                else if((u < 0 || u >= 1) && t > 0 && t < 1){
                    return T2_OVERLAP;
                }
                else if(u == 0 && t == 0){
                    return V_OVERLAP;
                }
            }
        }
    //no intersection
    intPoint = null;
    return NO_INTERSECTION;
    }

    private static void labelNodes_crossings(ArrayList<Node> subjNodes, ArrayList<Node> objNodes){
        // 1 - label sidedness, only needed for subject polygon
        for(Node subjStart : subjNodes){
            Node currentNode = subjStart;
            Node subjPrev, subjNext, objPrev, objNext;
            do{
                if(currentNode.isIntersection()){
                    subjPrev = currentNode.prev;
                    subjNext = currentNode.next;
                    objPrev = currentNode.cross.prev;
                    objNext = currentNode.cross.next;

                    int prevSide = getSide(subjPrev, currentNode, objPrev, objNext);
                    int nextSide = getSide(subjNext, currentNode, objPrev, objNext);

                    switch(prevSide){
                        case ON:
                            switch(nextSide){
                                case ON:
                                    currentNode.sidedness = Node.ON_ON;
                                    break;
                                case RIGHT:
                                    currentNode.sidedness = Node.ON_RIGHT;
                                    break;
                                case LEFT:
                                    currentNode.sidedness = Node.ON_LEFT;
                                    break;
                            }
                            break;
                        case RIGHT:
                            switch(nextSide){
                                case ON:
                                    currentNode.sidedness = Node.RIGHT_ON;
                                    break;
                                case RIGHT:
                                    currentNode.sidedness = Node.RIGHT_RIGHT;
                                    break;
                                case LEFT:
                                    currentNode.sidedness = Node.RIGHT_LEFT;
                                    break;
                            }
                            break;
                        case LEFT:
                            switch(nextSide){
                                case ON:
                                    currentNode.sidedness = Node.LEFT_ON;
                                    break;
                                case RIGHT:
                                    currentNode.sidedness = Node.LEFT_RIGHT;
                                    break;
                                case LEFT:
                                    currentNode.sidedness = Node.LEFT_LEFT;
                                    break;
                            }
                            break;
                    }
                }
                currentNode = currentNode.next;
            } while(currentNode != subjStart);
        }

        // 2 - find crossings on subject polygon, copy to object polygon
        for(Node subjStart : subjNodes){
            Node currentNode = subjStart;
            // loop #1 - label simple cases, and determine currentSide for next loop
            int currentSide = -1;
            do{
                if(currentNode.isIntersection()){
                    switch(currentNode.sidedness){
                        case Node.ON_LEFT:
                            //skip for first loop
                            break;
                        case Node.ON_RIGHT:
                            //skip for first loop
                            break;
                        case Node.RIGHT_LEFT:
                            currentNode.isCrossing = true;
                            currentNode.cross.isCrossing = true;
                            break;
                        case Node.LEFT_RIGHT:
                            currentNode.isCrossing = true;
                            currentNode.cross.isCrossing = true;
                            break;
                        case Node.RIGHT_ON:
                            //indicates side, but do not label
                            currentSide = RIGHT;
                            break;
                        case Node.LEFT_ON:
                            //indicates side, but do not label
                            currentSide = LEFT;
                            break;
                        default:
                            // the rest of cases: ON/ON, RIGHT/RIGHT, LEFT/LEFT are just bounces
                            break;
                    }
                }
                currentNode = currentNode.next;
            } while(currentNode != subjStart);

            currentNode = subjStart;
            // loop #2 - use currentSide to finish labels for delayed cases
            do{
                if(currentNode.isIntersection()){
                    switch(currentNode.sidedness){
                        case Node.ON_LEFT:
                            if(currentSide == -1){
                                System.out.println("ERROR: Unable to determine crossing label.");
                            } else if (currentSide == RIGHT){
                                currentNode.isCrossing = true;
                                currentNode.cross.isCrossing = true;
                            }
                            break;
                        case Node.ON_RIGHT:
                            if(currentSide == -1){
                                System.out.println("ERROR: Unable to determine crossing label.");
                            } else if (currentSide == LEFT){
                                currentNode.isCrossing = true;
                                currentNode.cross.isCrossing = true;
                            }
                            break;
                        case Node.RIGHT_ON:
                            currentSide = RIGHT;
                            break;
                        case Node.LEFT_ON:
                            currentSide = LEFT;
                            break;
                        default:
                            // all other cases should already be handled in previous loop
                            break;
                    }
                }
                currentNode = currentNode.next;
            } while(currentNode != subjStart);
        }
    }

    private static int getSide(Node subj, Node intersection, Node objPrev, Node objNext){
        if(subj.cross == objPrev){ return ON; }
        if(subj.cross == objNext){ return ON; }
        if(colinearTest(objPrev.pos, intersection.pos, objNext.pos) >= 0){
            //straight, or skews left
            if( (colinearTest(subj.pos, objPrev.pos, intersection.pos) > 0) && (colinearTest(subj.pos, intersection.pos, objNext.pos) > 0) ){
                return LEFT;
            }
            else{ return RIGHT; }
        }
        else {
            //skews right
            if( (colinearTest(subj.pos, objPrev.pos, intersection.pos) < 0) && (colinearTest(subj.pos, intersection.pos, objNext.pos) < 0) ){
                return RIGHT;
            }
            else{ return LEFT; }
        }
    }

    //TO-DO: not finding entry cases for internal holes properly! FIX!!
    private static void labelNodes_entries(ArrayList<Node> nodes, FPolygon poly){
        // label entry/exit
        for(Node start : nodes){
            Node currentNode = start;
            do{
                if(currentNode.isIntersection() && currentNode.isCrossing){
                    //test for entry case with midpoint of 'next' segment
                    //TO-DO: THIS TEST POINT CAN BE ON THE TEST POLY, IF CURRENTNODE HAS X-ON SIDEDNESS
                    PVector testPoint;
                    if((currentNode.sidedness != Node.RIGHT_ON) && (currentNode.sidedness != Node.LEFT_ON) && (currentNode.sidedness != Node.ON_ON) ){
                        testPoint = PVector.lerp(currentNode.pos, currentNode.next.pos, 0.5f);
                    } else {
                        Node testNode = currentNode.next;
                        while(testNode != currentNode){
                            if((testNode.sidedness != Node.RIGHT_ON) && (testNode.sidedness != Node.LEFT_ON) && (testNode.sidedness != Node.ON_ON) ){
                                break;
                            }
                            testNode = testNode.next;
                        }
                        testPoint = PVector.lerp(testNode.pos, testNode.next.pos, 0.5f);
                    }
                    if(isPointInPoly(testPoint, poly)){
                        currentNode.isEntry = true;
                    }
                    // do nothing for exit cases, defaults to false
                }
                currentNode = currentNode.next;
            } while(currentNode != start);
        }
    }

    private static float colinearTest(PVector a, PVector b, PVector c){
        return (a.x * (b.y - c.y)) + (b.x * (c.y - a.y)) + (c.x * (a.y - b.y));
    }

    private static FPolygon trace_and(ArrayList<Node> subjNodes, ArrayList<Node> objNodes, FPolygon obj, FPolygon subj){
        FPolygon result = new FPolygon();

        for(Node subjStart : subjNodes){
            tracePath_and(subjStart, obj, result);
        }
        // TO-DO: handle case in which subj encloses obj, not handled

        return result;
    }

    private static void tracePath_and(Node start, FPolygon otherPoly, FPolygon result){
        Node currentNode = start;

        int crossings = 0;      //track crossing intersections

        do{
            // if Node is an untraced crossing, begin a new path
            if(currentNode.isCrossing && !currentNode.isTraced()){
                crossings += 1;

                FPath path = new FPath();

                Node tracingNode = currentNode;
                path.appendVertex(tracingNode.pos);
                tracingNode.trace();

                boolean moveToNext;         //True for traverse forward, False for traverse backwards

                // for AND, follow entry points
                // if a Node is an entry point -> traverse forward, else backwards
                if(tracingNode.isEntry){
                    moveToNext = true;
                } else {
                    moveToNext = false;
                }

                if(moveToNext){
                    tracingNode = tracingNode.next;
                } else {
                    tracingNode = tracingNode.prev;
                }

                do{
                    path.appendVertex(tracingNode.pos);
                    tracingNode.trace();

                    if(tracingNode.isCrossing){
                        tracingNode = tracingNode.cross;
                        if(tracingNode.isEntry){
                            moveToNext = true;
                        } else {
                            moveToNext = false;
                        }
                    }

                    if(moveToNext){
                        tracingNode = tracingNode.next;
                    } else {
                        tracingNode = tracingNode.prev;
                    }

                }while(!tracingNode.isTraced());

                result.addContour(path);
            }
            currentNode = currentNode.next;
        } while(currentNode != start);

        //if no crossing intersections, find a midpoint to determine if it is inside or outside
        if(crossings == 0){
            currentNode = start;
            boolean confirmedInteriority = false;
            boolean isInside = false;
            do{
                if(currentNode.sidedness != Node.ON_ON && currentNode.sidedness != Node.LEFT_ON && currentNode.sidedness != Node.RIGHT_ON){
                    PVector a = currentNode.pos;
                    PVector b = currentNode.next.pos;
                    //get midpoint
                    PVector mid = PVector.lerp(a,b,0.5f);
                    isInside = isPointInPoly(mid, otherPoly);
                    confirmedInteriority = true;
                    break;
                }
                currentNode = currentNode.next;
            } while (currentNode != start);

            if(confirmedInteriority){
                if(isInside){
                    // subj path is inside obj
                    // traverse nodes and add to result
                    FPath path = new FPath();

                    Node tracingNode = currentNode;
                    path.appendVertex(tracingNode.pos);
                    tracingNode.trace();
                    tracingNode = tracingNode.next;
                    do{
                        path.appendVertex(tracingNode.pos);
                        tracingNode.trace();
                        tracingNode = tracingNode.next;
                    }while(!tracingNode.isTraced());

                    result.addContour(path);

                } else {
                    // subj path is entirely outside obj
                    // TO-DO!!! what if obj is enclosed in subj?
                }
            } else {
                // interiority unconfirmed
                // should be identical polygons? add to result
                FPath path = new FPath();

                Node tracingNode = currentNode;
                path.appendVertex(tracingNode.pos);
                tracingNode.trace();
                tracingNode = tracingNode.next;
                do{
                    path.appendVertex(tracingNode.pos);
                    tracingNode.trace();
                    tracingNode = tracingNode.next;
                }while(!tracingNode.isTraced());

                result.addContour(path);
            }
        }
    }

    private static FPolygon trace_or(ArrayList<Node> subjNodes, ArrayList<Node> objNodes, FPolygon obj, FPolygon subj){
        FPolygon result = new FPolygon();

        for(Node subjStart : subjNodes){
            tracePath_or(subjStart, obj, result);
        }
        for(Node objStart : objNodes){
            tracePath_or(objStart, subj, result);
        }

        return result;
    }

    private static void tracePath_or(Node start, FPolygon otherPoly, FPolygon result){
        Node currentNode = start;

        int crossings = 0;      //track crossing intersections

        do{
            // if Node is an untraced crossing, begin a new path
            if(currentNode.isCrossing && !currentNode.isTraced()){
                crossings += 1;

                FPath path = new FPath();

                Node tracingNode = currentNode;
                path.appendVertex(tracingNode.pos);
                tracingNode.trace();

                boolean moveToNext;         //True for traverse forward, False for traverse backwards

                // for OR, avoid entry points
                // if a Node is an entry point -> traverse backwards, else forwards
                if(tracingNode.isEntry){
                    moveToNext = false;
                } else {
                    moveToNext = true;
                }

                if(moveToNext){
                    tracingNode = tracingNode.next;
                } else {
                    tracingNode = tracingNode.prev;
                }

                do{
                    path.appendVertex(tracingNode.pos);
                    tracingNode.trace();

                    if(tracingNode.isCrossing){
                        tracingNode = tracingNode.cross;
                        if(tracingNode.isEntry){
                            moveToNext = false;
                        } else {
                            moveToNext = true;
                        }
                    }

                    if(moveToNext){
                        tracingNode = tracingNode.next;
                    } else {
                        tracingNode = tracingNode.prev;
                    }

                }while(!tracingNode.isTraced());

                result.addContour(path);
            }
            currentNode = currentNode.next;
        } while(currentNode != start);

        //if no crossing intersections, find a midpoint to determine if it is inside or outside
        if(crossings == 0){
            currentNode = start;
            boolean confirmedInteriority = false;
            boolean isInside = false;
            do{
                if(currentNode.sidedness != Node.ON_ON && currentNode.sidedness != Node.LEFT_ON && currentNode.sidedness != Node.RIGHT_ON){
                    PVector a = currentNode.pos;
                    PVector b = currentNode.next.pos;
                    //get midpoint
                    PVector mid = PVector.lerp(a,b,0.5f);
                    isInside = isPointInPoly(mid, otherPoly);
                    confirmedInteriority = true;
                    break;
                }
                currentNode = currentNode.next;
            } while (currentNode != start);

            if(confirmedInteriority){
                if(isInside){
                    // subj path is inside obj
                    // do nothing

                } else {
                    // subj path is outside obj
                    // traverse nodes and add to result
                    FPath path = new FPath();

                    Node tracingNode = currentNode;
                    path.appendVertex(tracingNode.pos);
                    tracingNode.trace();
                    tracingNode = tracingNode.next;
                    do{
                        path.appendVertex(tracingNode.pos);
                        tracingNode.trace();
                        tracingNode = tracingNode.next;
                    }while(!tracingNode.isTraced());

                    result.addContour(path);
                }
            } else {
                // interiority unconfirmed
                // should be identical polygons? add result
                FPath path = new FPath();

                Node tracingNode = currentNode;
                path.appendVertex(tracingNode.pos);
                tracingNode.trace();
                tracingNode = tracingNode.next;
                do{
                    path.appendVertex(tracingNode.pos);
                    tracingNode.trace();
                    tracingNode = tracingNode.next;
                }while(!tracingNode.isTraced());

                result.addContour(path);
            }
        }
    }


    private static FPolygon trace_not(ArrayList<Node> subjNodes, ArrayList<Node> objNodes, FPolygon obj, FPolygon subj){
        FPolygon result = new FPolygon();

        for(Node subjStart : subjNodes){
            tracePath_not(subjStart, obj, result);
        }

        return result;
    }

    private static void tracePath_not(Node start, FPolygon otherPoly, FPolygon result){
        Node currentNode = start;

        int crossings = 0;      //track crossing intersections

        do{
            // if Node is an untraced crossing, begin a new path
            if(currentNode.isCrossing && !currentNode.isTraced()){
                crossings += 1;

                FPath path = new FPath();

                Node tracingNode = currentNode;
                path.appendVertex(tracingNode.pos);
                tracingNode.trace();

                boolean moveToNext;         //True for traverse forward, False for traverse backwards
                boolean onSubj = true;

                // for NOT, avoid entry points as subj
                // if a Node is an entry point -> traverse backwards, else forwards
                if(tracingNode.isEntry){
                    moveToNext = false;
                } else {
                    moveToNext = true;
                }

                if(moveToNext){
                    tracingNode = tracingNode.next;
                } else {
                    tracingNode = tracingNode.prev;
                }

                do{
                    path.appendVertex(tracingNode.pos);
                    tracingNode.trace();

                    if(tracingNode.isCrossing){
                        tracingNode = tracingNode.cross;
                        onSubj = !onSubj;

                        if(tracingNode.isEntry){
                            // traverse backwards for subj entry points, and forward for obj entry points
                            moveToNext = !onSubj;
                        } else {
                            // traverse forwards for subj entry points, and backwards for obj entry points
                            moveToNext = onSubj;
                        }
                    }

                    if(moveToNext){
                        tracingNode = tracingNode.next;
                    } else {
                        tracingNode = tracingNode.prev;
                    }

                }while(!tracingNode.isTraced());

                result.addContour(path);
            }
            currentNode = currentNode.next;
        } while(currentNode != start);

        //if no crossing intersections, find a midpoint to determine if it is inside or outside
        if(crossings == 0){
            currentNode = start;
            boolean confirmedInteriority = false;
            boolean isInside = false;
            do{
                if(currentNode.sidedness != Node.ON_ON && currentNode.sidedness != Node.LEFT_ON && currentNode.sidedness != Node.RIGHT_ON){
                    PVector a = currentNode.pos;
                    PVector b = currentNode.next.pos;
                    //get midpoint
                    PVector mid = PVector.lerp(a,b,0.5f);
                    isInside = isPointInPoly(mid, otherPoly);
                    confirmedInteriority = true;
                    break;
                }
                currentNode = currentNode.next;
            } while (currentNode != start);

            if(confirmedInteriority){
                if(isInside){
                    // subj path is inside obj
                    // do nothing

                } else {
                    // subj path is outside obj
                    // traverse nodes and add to result
                    FPath path = new FPath();

                    Node tracingNode = currentNode;
                    path.appendVertex(tracingNode.pos);
                    tracingNode.trace();
                    tracingNode = tracingNode.next;
                    do{
                        path.appendVertex(tracingNode.pos);
                        tracingNode.trace();
                        tracingNode = tracingNode.next;
                    }while(!tracingNode.isTraced());

                    result.addContour(path);
                }
            } else {
                // interiority unconfirmed
                // should be identical polygons? add result
                FPath path = new FPath();

                Node tracingNode = currentNode;
                path.appendVertex(tracingNode.pos);
                tracingNode.trace();
                tracingNode = tracingNode.next;
                do{
                    path.appendVertex(tracingNode.pos);
                    tracingNode.trace();
                    tracingNode = tracingNode.next;
                }while(!tracingNode.isTraced());

                result.addContour(path);
            }
        }
    }


    private static void drawNodes(ArrayList<Node> nodes, PApplet app, float multiplier){
        app.pushStyle();
        app.noFill();
        app.strokeWeight(2);
        for(Node n : nodes){
            Node temp = n;
            do{
                if(temp.isIntersection() && temp.isCrossing){
                    if(temp.isEntry){
                        app.stroke(0xffD10E3C); //red
                    } else {
                        app.stroke(0xff58aed1); //blue
                    }
                } else {
                    app.stroke(0xff3CD10E); //green
                }
                app.ellipse(temp.pos.x, temp.pos.y, 12.0f*multiplier, 12.0f*multiplier);
                temp = temp.next;
            } while(temp != n);
        }
        app.popStyle();
    }

}