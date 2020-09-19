package frdk.geom;

import processing.core.*;
import java.util.ArrayList;

//GeomManager?
/*
    TO-DO LIST:

    PShape Loading
    SVG loading?

    boolean functions:
        Make for FPoly/FPoly, FPoly/FGroup, FGroup/FPoly, and FGroup/FGroup
        xor function

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
    public static final int NO_INTERSECTION = 0;
    // non-parallel cases
    public static final int X_INTERSECTION = 1;    //at mid of ab and cd, most common
    public static final int T1_INTERSECTION = 2;   //at start of ab
    public static final int T2_INTERSECTION = 3;   //at start of cd
    public static final int V_INTERSECTION = 4;    //at start of ab and cd
    // colinear cases
    public static final int X_OVERLAP = 5;         //both start on line
    public static final int T1_OVERLAP = 6;        //a on cd
    public static final int T2_OVERLAP = 7;        //c on ab
    public static final int V_OVERLAP = 8;         //a and c share start point

    //--- SIDE FLAGS ---//
    static final int ON = 0;
    static final int LEFT = 1;
    static final int RIGHT = 2;

    static final float epsilon = .00001f;  //accuracy range of calculations, tinker with for best results
    static final PVector testDir = new PVector(1.0f, 0.0f);

    static public int segments = 32;

    //--- IMPORT FUNCTIONS ---

    public static FGroup PShapeToGroup(PShape pshape){
        FGroup newGroup = new FGroup();

        int childCount = pshape.getChildCount();
        for(int i = 0; i < childCount; i++){
            PShape child = pshape.getChild(i);
            int family = child.getFamily();
            int kind = child.getKind();
            
            // for GROUP
            if (family == PShape.GROUP){
                System.out.println("GROUP START");
                newGroup.appendChild(PShapeToGroup(child));
                System.out.println("GROUP END");
                System.out.println();
            }

            // for PRIMITIVEs
            if (family == PShape.PRIMITIVE){
                System.out.println("PRIMITIVE");
                float[] params = child.getParams();
                
                FPath currentPath = new FPath();
                FPolygon poly = new FPolygon();

                switch(kind){
                    // ELLIPSE, RECT, ARC, TRIANGLE, QUAD, POINT or LINE
                    // BOX and SPHERE are 3D only, with 3D LINEs and POINTs not supported
                    case PShape.ELLIPSE:
                        // ellipse(a,b,c,d)
                        // I cannot determine if ellipse is using CORNER or CENTER mode
                        // depends on shape's origin?
                        System.out.println("ELLIPSE: " + params.length + " params");
                        poly = new FPolygon( getPoints_ellipse( params[0], params[1], params[2], params[3] ) );
                        newGroup.appendChild(poly);
                        break;

                    case PShape.RECT:
                        System.out.println("RECT: " + params.length + " params");
                        if(params.length == 4){
                            poly = new FPolygon( getPoints_rect( params[0], params[1], params[2], params[3] ) );
                        } else if (params.length == 5){
                            poly = new FPolygon( getPoints_rectRadius( 
                                params[0], params[1], params[2], params[3],
                                params[4], params[4], params[4], params[4] ) );
                            //rect(a, b, c, d, r)
                        } else if (params.length == 8){
                            poly = new FPolygon( getPoints_rectRadius( 
                                params[0], params[1], params[2], params[3],
                                params[4], params[5], params[6], params[7] ) );
                            //rect(a, b, c, d, tl, tr, br, bl)
                        }
                        newGroup.appendChild(poly);
                        break;

                    case PShape.ARC:
                        System.out.println("ARC: " + params.length + " params");
                        // arc(a, b, c, d, start, stop)
                        // arc(a, b, c, d, start, stop, mode)
                        // mode: nothing(treat as OPEN), OPEN, CHORD, or PIE
                        int mode = 0;
                        if(params.length == 6){
                            mode = PShape.OPEN;
                            currentPath.appendVertexArray( getPoints_arc( 
                                params[0], params[1], params[2], params[3],
                                params[4], params[5], mode ) );
                        } else if (params.length == 7){
                            mode = (int)params[6];
                            currentPath.appendVertexArray( getPoints_arc( 
                                params[0], params[1], params[2], params[3],
                                params[4], params[5], mode ) );
                        }

                        if((mode == PShape.CHORD) || (mode == PShape.PIE)){
                            //CHORD and PIE are closed polys
                            poly.addContour(currentPath);
                            newGroup.appendChild(poly);
                        } else {
                            // OPEN is an open path
                            newGroup.appendChild(currentPath);
                        }
                        break;

                    case PShape.TRIANGLE:
                        //triangle(x1,y1,x2,y2,x3,y3)
                        System.out.println("TRIANGLE: " + params.length + " params");
                        poly = new FPolygon( getPoints_triangle(
                            params[0], params[1], params[2], params[3], params[4], params[5] ) );
                        newGroup.appendChild(poly);
                        break;

                    case PShape.QUAD:
                        System.out.println("QUAD: " + params.length + " params");
                        // quad(x1, y1, x2, y2, x3, y3, x4, y4)
                        poly = new FPolygon( getPoints_quad(
                            params[0], params[1], params[2], params[3], 
                            params[4], params[5], params[6], params[7] ) );
                        newGroup.appendChild(poly);
                        break;

                    case PShape.LINE:
                        System.out.println("LINE: " + params.length + " params");
                        // line(x1, y1, x2, y2)
                        currentPath.appendVertex( params[0], params[1] );
                        currentPath.appendVertex( params[2], params[3] );
                        newGroup.appendChild(currentPath);
                        break;

                    case PShape.POINT:
                        System.out.println("POINT: " + params.length + " params");
                        // point(x, y)
                        currentPath.appendVertex( params[0], params[1] );
                        newGroup.appendChild(currentPath);
                        break;

                    default:
                        break;
                }
                System.out.println();
            }
            
            // for PATHs
            if (family == PShape.PATH){
                // Print info
                System.out.println("PATH");
                System.out.println("VertCount: " + child.getVertexCount());
                newGroup.appendChild(getPoints_path(child));
            }

            // for GEOMETRY
            if (family == PShape.GEOMETRY){

                System.out.println("GEOMETRY");
                System.out.println("VertCount: " + child.getVertexCount());
                System.out.println("VertCodeCount: " + child.getVertexCodeCount());

                FPath currentPath = new FPath();
                FPolygon poly = new FPolygon();

                switch(kind){
                    case PShape.POLYGON:
                        System.out.println("POLYGON: ");
                        //equivalent of a PATH shape
                        newGroup.appendChild( getPoints_path(child) );
                        break;
                    case PShape.POINTS:
                        System.out.println("POINTS: ");
                        //treat as group of single length paths
                        FGroup pointGroup = new FGroup();
                        for(int j = 0; j < child.getVertexCount(); j++){
                            currentPath.appendVertex( child.getVertexX(j), child.getVertexY(j) );
                            pointGroup.appendChild(currentPath);
                            currentPath = new FPath();
                        }
                        newGroup.appendChild(pointGroup);
                        break;
                    case PShape.LINES:
                        System.out.println("LINES: ");
                        // group of 2-length paths
                        FGroup lineGroup = new FGroup();
                        for(int j = 0; j < child.getVertexCount()-1; j+=2){
                            currentPath.appendVertex( child.getVertexX(j), child.getVertexY(j) );
                            currentPath.appendVertex( child.getVertexX(j+1), child.getVertexY(j+1) );
                            lineGroup.appendChild(currentPath);
                            currentPath = new FPath();
                        }
                        newGroup.appendChild(lineGroup);
                        break;
                    case PShape.TRIANGLES:
                        System.out.println("TRIANGLES: ");
                        // group of closed polys, 3 vertices each
                        FGroup triGroup = new FGroup();
                        for(int j = 0; j < child.getVertexCount()-2; j+=3){
                            currentPath.appendVertex( child.getVertexX(j), child.getVertexY(j) );
                            currentPath.appendVertex( child.getVertexX(j+1), child.getVertexY(j+1) );
                            currentPath.appendVertex( child.getVertexX(j+2), child.getVertexY(j+2) );
                            poly.addContour(currentPath);
                            triGroup.appendChild(poly);
                            currentPath = new FPath();
                            poly = new FPolygon();
                        }
                        newGroup.appendChild(triGroup);
                        break;
                    case PShape.TRIANGLE_STRIP:
                        System.out.println("TRIANGLE_STRIP: ");
                        // group of closed polys, use previous 2 vertices plus 1 new each time
                        FGroup triStripGroup = new FGroup();
                        for(int j = 0; j < child.getVertexCount()-2; j+=1){
                            currentPath.appendVertex( child.getVertexX(j), child.getVertexY(j) );
                            currentPath.appendVertex( child.getVertexX(j+1), child.getVertexY(j+1) );
                            currentPath.appendVertex( child.getVertexX(j+2), child.getVertexY(j+2) );
                            poly.addContour(currentPath);
                            triStripGroup.appendChild(poly);
                            currentPath = new FPath();
                            poly = new FPolygon();
                        }
                        newGroup.appendChild(triStripGroup);
                        break;
                    case PShape.TRIANGLE_FAN:
                        System.out.println("TRIANGLE_FAN: ");
                        // save first vertex, make group of closed polys with first plus every 2 vertices
                        FGroup triFanGroup = new FGroup();
                        for(int j = 1; j < child.getVertexCount()-1; j+=1){
                            currentPath.appendVertex( child.getVertexX(0), child.getVertexY(0) );
                            currentPath.appendVertex( child.getVertexX(j), child.getVertexY(j) );
                            currentPath.appendVertex( child.getVertexX(j+1), child.getVertexY(j+1) );
                            poly.addContour(currentPath);
                            triFanGroup.appendChild(poly);
                            currentPath = new FPath();
                            poly = new FPolygon();
                        }
                        newGroup.appendChild(triFanGroup);
                        break;
                    case PShape.QUADS:
                        System.out.println("QUADS: ");
                        // group of closed polys, 4 vertices each
                        FGroup quadGroup = new FGroup();
                        for(int j = 0; j < child.getVertexCount()-3; j+=4){
                            currentPath.appendVertex( child.getVertexX(j), child.getVertexY(j) );
                            currentPath.appendVertex( child.getVertexX(j+1), child.getVertexY(j+1) );
                            currentPath.appendVertex( child.getVertexX(j+2), child.getVertexY(j+2) );
                            currentPath.appendVertex( child.getVertexX(j+3), child.getVertexY(j+3) );
                            poly.addContour(currentPath);
                            quadGroup.appendChild(poly);
                            currentPath = new FPath();
                            poly = new FPolygon();
                        }
                        newGroup.appendChild(quadGroup);
                        break;
                    case PShape.QUAD_STRIP:
                        System.out.println("QUAD_STRIP: ");
                        // group of closed polys, use previous 2 vertices plus 2 new each time
                        FGroup quadStripGroup = new FGroup();
                        for(int j = 0; j < child.getVertexCount()-3; j+=2){
                            currentPath.appendVertex( child.getVertexX(j), child.getVertexY(j) );
                            currentPath.appendVertex( child.getVertexX(j+1), child.getVertexY(j+1) );
                            currentPath.appendVertex( child.getVertexX(j+3), child.getVertexY(j+3) );
                            currentPath.appendVertex( child.getVertexX(j+2), child.getVertexY(j+2) );
                            poly.addContour(currentPath);
                            quadStripGroup.appendChild(poly);
                            currentPath = new FPath();
                            poly = new FPolygon();
                        }
                        newGroup.appendChild(quadStripGroup);
                        break;
                    default:
                        break;
                }

                if(child.isClosed()){
                    poly.addContour(currentPath);
                    newGroup.appendChild(poly);
                } else {
                    newGroup.appendChild(currentPath);
                }

                for(int j = 0; j < child.getVertexCount(); j++){
                    System.out.println(child.getVertexX(j) + ", " + child.getVertexY(j));
                }
                System.out.println();
            }
            
        }
        return newGroup;
    }

    private static FShape getPoints_path(PShape child){
        int vertCodeCount = child.getVertexCodeCount();
        int[] vertCodes = child.getVertexCodes();

        // Construct Path
        FPath currentPath = new FPath();
        FPolygon poly = new FPolygon();

        if(vertCodeCount == 0){
            //if no vertex codes, all are regular vertices
            for(int j = 0; j < child.getVertexCount(); j++){
                currentPath.appendVertex( child.getVertexX(j), child.getVertexY(j) );
            }
        } else {
            int vIndex = 0; // tracks current vertex index
            for(int j = 0; j < vertCodeCount; j++){
                switch (vertCodes[j]){
                    case PShape.VERTEX:
                        currentPath.appendVertex(child.getVertexX(vIndex), child.getVertexY(vIndex));
                        vIndex += 1;
                        break;
                    case PShape.BEZIER_VERTEX:
                        currentPath.appendVertexArray(getPoints_cubic( 
                            child.getVertexX(vIndex-1), child.getVertexY(vIndex-1),
                            child.getVertexX(vIndex), child.getVertexY(vIndex),
                            child.getVertexX(vIndex+1), child.getVertexY(vIndex+1),
                            child.getVertexX(vIndex+2), child.getVertexY(vIndex+2) 
                            ));
                        vIndex += 3;
                        break;
                    case PShape.QUADRATIC_VERTEX:
                        currentPath.appendVertexArray(getPoints_quadratic( 
                            child.getVertexX(vIndex-1), child.getVertexY(vIndex-1),
                            child.getVertexX(vIndex), child.getVertexY(vIndex),
                            child.getVertexX(vIndex+1), child.getVertexY(vIndex+1) 
                            ));
                        vIndex += 2;
                        break;
                    case PShape.CURVE_VERTEX:
                        // centripetal Catmull-Rom Spline calculation not implemented yet
                        // OR NEVER LOL WHO CARES
                        System.out.println("CURVE_VERTEX not implemented");
                        vIndex += 2;
                        break;
                    case PShape.BREAK:
                        poly.addContour(currentPath);
                        currentPath = new FPath();
                        break;
                }
            }
        }
        if(child.isClosed()){
            poly.addContour(currentPath);
            return poly;
        } else {
            return currentPath;
        }
    }

    private static PVector[] getPoints_cubic(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3){
        PVector[] points = new PVector[segments];
        float t = 0;

        for(int i = 0; i < segments-1; i++) {
            t = (1.0f/(float)segments) * (i+1);

            float p0x = (1-t) * (1-t) * (1-t) * x0;
            float p1x = (1-t) * (1-t) * (t) * 3 * x1;
            float p2x = (1-t) * (t) * (t) * 3 * x2;
            float p3x = (t) * (t) * (t) * x3;

            float p0y = (1-t) * (1-t) * (1-t) * y0;
            float p1y = (1-t) * (1-t) * (t) * 3 * y1;
            float p2y = (1-t) * (t) * (t) * 3 * y2;
            float p3y = (t) * (t) * (t) * y3;

            float x = p0x + p1x + p2x + p3x;
            float y = p0y + p1y + p2y + p3y;

            points[i] = new PVector(x, y);
        }
        points[segments-1] = new PVector(x3, y3);

        return points;
    }

    private static PVector[] getPoints_quadratic(float x0, float y0, float x1, float y1, float x2, float y2){
        PVector[] points = new PVector[segments];
        float t = 0;

        for(int i = 0; i < segments-1; i++) {
            t = (1.0f/(float)segments) * (i+1);

            float p0x = (1-t) * (1-t) * x0;
            float p1x = (1-t) * (t) * 2 * x1;
            float p2x = (t) * (t) * x2;

            float p0y = (1-t) * (1-t) * y0;
            float p1y = (1-t) * (t) * 2 * y1;
            float p2y = (t) * (t) * y2;

            float x = p0x + p1x + p2x;
            float y = p0y + p1y + p2y;

            points[i] = new PVector(x, y);
        }
        points[segments-1] = new PVector(x2, y2);

        return points;
    }

    private static PVector[] getPoints_ellipse(float cx, float cy, float dx, float dy){
        PVector[] points = new PVector[segments * 4];
        double t = 0;
        for(int i = 0; i < segments * 4; i++) {
            t = ( (2*Math.PI)/(double)(segments * 4) ) * i;
            double x = (dx/2.0f) * Math.cos(t);
            x += cx + dx/2.0f;
            double y = (dy/2.0f) * Math.sin(t);
            y += cy + dy/2.0f;
            points[i] = new PVector((float)x, (float)y);
        }
        return points;
    }

    private static PVector[] getPoints_rect(float a, float b, float c, float d){
        PVector[] points = new PVector[4];
        points[0] = new PVector(a, b);
        points[1] = new PVector(a + c, b);
        points[2] = new PVector(a + c, b + d);
        points[3] = new PVector(a, b + d);
        return points;
    }
    private static PVector[] getPoints_rectRadius(float a, float b, float c, float d, float tl, float tr, float br, float bl){
        PVector[] points = new PVector[4 * (segments+1)];
        PVector[] corner;

        corner = getPoints_arc(a+tl, b+tl, tl*2, tl*2, PShape.PI, PShape.PI+PShape.HALF_PI, PShape.OPEN);
        System.arraycopy(corner, 0, points, 0, segments+1);
        corner = getPoints_arc(a+c-tr, b+tr, tr*2, tr*2, PShape.PI+PShape.HALF_PI, PShape.TWO_PI, PShape.OPEN);
        System.arraycopy(corner, 0, points, 1*(segments+1), segments+1);
        corner = getPoints_arc(a+c-br, b+d-br, br*2, br*2, 0, PShape.HALF_PI, PShape.OPEN);
        System.arraycopy(corner, 0, points, 2*(segments+1), segments+1);
        corner = getPoints_arc(a+bl, b+d-bl, bl*2, bl*2, PShape.HALF_PI, PShape.PI, PShape.OPEN);
        System.arraycopy(corner, 0, points, 3*(segments+1), segments+1);

        return points;
    }

    // mode: OPEN, CHORD, or PIE
    private static PVector[] getPoints_arc(float cx, float cy, float dx, float dy, float start, float stop, int mode){
        PVector[] points;
        
        if(mode == PShape.PIE){
            //need an extra point for PIE
            points = new PVector[segments+2];
        } else {
            points = new PVector[segments+1];
        }

        double t = 0;
        for(int i = 0; i < segments+1; i++) {
            t = (( (stop-start)/(double)segments ) * i) + start;

            float x = dx/2 * (float)Math.cos(t);
            x += cx;
            float y = dy/2 * (float)Math.sin(t);
            y += cy;
            points[i] = new PVector(x, y);
        }

        if(mode == PShape.PIE){
            //add center point for PIE
            points[segments+1] = new PVector(cx, cy);
        }

        return points;
    }

    private static PVector[] getPoints_triangle(float x1, float y1, float x2, float y2, float x3, float y3){
        PVector[] points = new PVector[3];
        points[0] = new PVector(x1, y1);
        points[1] = new PVector(x2, y2);
        points[2] = new PVector(x3, y3);
        return points;
    }
    
    private static PVector[] getPoints_quad(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4){
        PVector[] points = new PVector[4];
        points[0] = new PVector(x1, y1);
        points[1] = new PVector(x2, y2);
        points[2] = new PVector(x3, y3);
        points[3] = new PVector(x4, y4);
        return points;
    }

    // Implement if PShape functionality is lacking...
    // SVG import ref: https://github.com/rikrd/geomerative/blob/master/src/geomerative/
    /*
    public FShape XMLtoGroup(XML xml){
        XML elems[] = xml.getChildren();
        for (int i = 0; i < elems.length; i++) {
            String name = elems[i].getName().toLowerCase();
            XML element = elems[i];

            // Parse and create the geometrical element
            FShape geomElem = null;
            if(name.equals("g")){
                geomElem = elemToGroup(element);

            } else if (name.equals("path")) {
                geomElem = elemToShape(element);

            } else if(name.equals("polygon")){
                geomElem = elemToPolygon(element);

            } else if(name.equals("polyline")){
                geomElem = elemToPolyline(element);

            } else if(name.equals("circle")){
                geomElem = elemToCircle(element);

            } else if(name.equals("ellipse")){
                geomElem = elemToEllipse(element);

            } else if(name.equals("rect")){
                geomElem = elemToRect(element);

            } else if(name.equals("line")){
                geomElem = elemToLine(element);

            } else if(name.equals("defs")){
                // Do nothing normally we should make a hashmap
                // to apply everytime they are called in the actual objects
            } else{
                PApplet.println("Element '" + name + "' not known, ignored.");
            }

            // If the geometrical element has been correctly created
            if((geomElem != null)){
                // Transform geometrical element
                if(element.hasAttribute("transform")){
                String transformString = element.getString("transform");
                RMatrix transf = new RMatrix(transformString);
                geomElem.transform(transf);
                }

                // Get the style for the geometrical element
                grp.addElement(geomElem);
            }
        }

        return grp;
    }
    */

    //--- INTERSECTION FUNCTIONS ---

    public static int lineToLineIntersection(PVector a, PVector b, PVector c, PVector d, PVector intPoint){
        PVector ab = new PVector(b.x - a.x, b.y - a.y);
        PVector cd = new PVector(d.x - c.x, d.y - c.y);

        // catch zero length segments
        if((ab.mag() == 0.0) || (cd.mag() == 0.0)){
            return -1;
        }
        
        //based on equation:
        //ab * t + a = cd * u + c , which leads to:
        //(cd.y * (a.x - c.x)) - (cd.x * (a.y - c.y)) = t * ((ab.y * cd.x) - (ab.x * cd.y))
        //(ab.y * (a.x - c.x)) - (ab.x * (a.y - c.y)) = u * ((ab.y * cd.x) - (ab.x * cd.y))

        // 2nd half of above calculation
        // tests slopes: parallel if this equals 0
        float t = (ab.y * cd.x) - (ab.x * cd.y);
        float u = (ab.y * cd.x) - (ab.x * cd.y);

        float nearZero = 0.0f + epsilon;
        float nearOne = 1.0f - epsilon;

        // non-parallel cases:
        if( PApplet.abs(t) > 0 ) {
            t = ((cd.y * (a.x - c.x)) - (cd.x * (a.y - c.y))) / t;  //complete t calculation
            u = ((ab.y * (a.x - c.x)) - (ab.x * (a.y - c.y))) / u;  //complete u calculation

            if( (t > nearZero && t < nearOne) && (u > nearZero && u < nearOne) ) {
                intPoint.set((ab.x * t) + a.x, (ab.y * t) + a.y);
                return X_INTERSECTION;
            }
            else if((Math.abs(t) <= nearZero) && (u > nearZero && u < nearOne)){
                intPoint.set(a.x, a.y);
                return T1_INTERSECTION;
            }
            else if((t > nearZero && t < nearOne) && (Math.abs(u) <= nearZero)){
                intPoint.set(c.x, c.y);
                return T2_INTERSECTION;
            }
            else if((Math.abs(t) <= nearZero) && (Math.abs(u) <= nearZero)){
                intPoint.set(a.x, a.y);
                return V_INTERSECTION;
            }
        }
        else {
            //test for colinearity, using triangle area calculation
            if(colinearTest(a, b, c) == 0 && (colinearTest(a, b, d) == 0)){
                // parallel cases:
                // t and u now reflect the position of a and c relative to each other
                if(ab.x != 0.0f){
                    t = (c.x - a.x)/ab.x;
                } else {
                    t = (c.y - a.y)/ab.y;
                }
                if(cd.x != 0.0f){
                    u = (a.x - c.x)/cd.x;
                } else {
                    u = (a.y - c.y)/cd.y;
                }

                if( (t > nearZero && t < nearOne) && (u > nearZero && u < nearOne) ){
                    intPoint.set(a.x, a.y);
                    return X_OVERLAP;
                }
                else if(( (Math.abs(t) <= nearZero) || (Math.abs(t) >= nearOne) ) && (u > nearZero && u < nearOne)){
                    intPoint.set(c.x, c.y);
                    return T1_OVERLAP;
                }
                else if(( (Math.abs(u) <= nearZero) || (Math.abs(u) >= nearOne) ) && (t > nearZero && t < nearOne)){
                    intPoint.set(a.x, a.y);
                    return T2_OVERLAP;
                }
                else if((Math.abs(t) <= nearZero) && (Math.abs(u) <= nearZero)){
                    intPoint.set(a.x, a.y);
                    return V_OVERLAP;
                }
            }
        }
    //no intersection
    intPoint = null;
    return NO_INTERSECTION;
    }

    public static int rayToLineIntersection(PVector a, PVector b, PVector c, PVector d, PVector intPoint){
        PVector ab = new PVector(b.x - a.x, b.y - a.y);  //ab is the ray
        PVector cd = new PVector(d.x - c.x, d.y - c.y);  //cd is the line
        
        //based on equation:
        //ab * t + a = cd * u + c , which leads to:
        //(cd.y * (a.x - c.x)) - (cd.x * (a.y - c.y)) = t * ((ab.y * cd.x) - (ab.x * cd.y))
        //(ab.y * (a.x - c.x)) - (ab.x * (a.y - c.y)) = u * ((ab.y * cd.x) - (ab.x * cd.y))

        // 2nd half of above calculation
        // tests slopes: parallel if this equals 0
        float t = (ab.y * cd.x) - (ab.x * cd.y);
        float u = (ab.y * cd.x) - (ab.x * cd.y);

        float nearZero = 0.0f + epsilon;
        float nearOne = 1.0f - epsilon;

        // non-parallel cases:
        if( PApplet.abs(t) > 0 ) {
            t = ((cd.y * (a.x - c.x)) - (cd.x * (a.y - c.y))) / t;  //complete t calculation
            u = ((ab.y * (a.x - c.x)) - (ab.x * (a.y - c.y))) / u;  //complete u calculation

            // mid of ray and line
            if( (t > nearZero) && (u > nearZero && u < nearOne) ) {
                intPoint.set((ab.x * t) + a.x, (ab.y * t) + a.y);
                return X_INTERSECTION;
            }
            // start of ray, mid-line
            else if((Math.abs(t) <= nearZero) && (u > nearZero && u < nearOne)){
                intPoint.set(a.x, a.y);
                return T1_INTERSECTION;
            }
            // start of line, mid-ray
            // add (|| u==1) to account for endedness (?)
            else if((t > nearZero) && (Math.abs(u) <= nearZero)){
                intPoint.set(c.x, c.y);
                return T2_INTERSECTION;
            }
            // start of both
            else if((Math.abs(t) <= nearZero) && (Math.abs(u) <= nearZero)){
                intPoint.set(a.x, a.y);
                return V_INTERSECTION;
            }
        }
        else {
            //test for colinearity, using triangle area calculation
            if( (colinearTest(a, b, c) == 0) && (colinearTest(a, b, d) == 0) ){
                // parallel cases:
                // t and u now reflect the position of a and c relative to each other
                if(ab.x != 0.0f){
                    t = (c.x - a.x)/ab.x;
                } else {
                    t = (c.y - a.y)/ab.y;
                }
                if(cd.x != 0.0f){
                    u = (a.x - c.x)/cd.x;
                } else {
                    u = (a.y - c.y)/cd.y;
                }

                // c is in front of ray
                // additional cases are meaningless...
                if(t > nearZero){
                    intPoint = null;
                    return X_OVERLAP;
                }
                //c is on a
                else if((Math.abs(t) <= nearZero) && (Math.abs(u) <= nearZero)){
                    intPoint.set(a.x, a.y);
                    return V_OVERLAP;
                }
            }
        }
    //no intersection
    intPoint = null;
    return NO_INTERSECTION;
    }

    // TO-DO: make more robust, account for literal edge and corner cases
    public static boolean isPointInPoly(PVector point, FPolygon poly){
        PVector intPoint = new PVector();
        int crossCount = 0;
        PVector[] verts;
        for(int h = 0; h < poly.contourCount(); h++){
            verts = poly.getContour(h).getVerts();
            for (int i = 0; i < verts.length; i++) {
                PVector v1 = verts[i];
                PVector v2 = verts[ (i+1) % verts.length ];

                int result = rayToLineIntersection(point, PVector.add(point, testDir), v1, v2, intPoint);
                if( result == X_INTERSECTION ) {
                    crossCount++;
                } else if (result == T2_INTERSECTION) {
                    // find previous vertex, v0, that is not colinear to ray
                    // test if previous vertex, v0, is on the opposite side of v2
                    PVector v0 = verts[ (i + verts.length - 1) % verts.length ];
                    for(int j = 0; j < verts.length; j++){
                        if(colinearTest(point, v1, v0) != 0){ break; };
                        v0 = verts[ (i + verts.length - (j+1)) % verts.length ];
                    }
                    boolean testv0 = colinearTest(point, v1, v0) > 0;
                    boolean testv2 = colinearTest(point, v1, v2) > 0;    // True if skews left

                    // if v0 and v2 are on different sides of ray, add to count
                    if( (testv0 && !testv2) || (!testv0 && testv2) ){
                        crossCount++;
                    }
                }
            }
        }
        crossCount = crossCount % 2;
        return ( crossCount > 0 );
    }

    //Should this be FShape/FShape intersection?
    public static ArrayList<PVector> findIntersectionPoints(FPolygon p1, FPolygon p2){
        ArrayList<PVector> intPoints = new ArrayList<PVector>();

        PVector a, b, c, d;
        PVector[] p1Contour;
        PVector[] p2Contour;

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
                        
                        int result = lineToLineIntersection(a,b,c,d,intPoint);
                        if ( (result > NO_INTERSECTION) & (result < X_OVERLAP) ) {
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

        //contours check
        PVector[] contour;
        for(int i = 0; i < poly.contourCount(); i++){
            contour = poly.getContour(i).getVerts();
            for(int j = 0; j < contour.length; j++){
                c = contour[j];
                d = contour[(j+1) % contour.length];
                PVector intPoint = new PVector();

                int result = lineToLineIntersection(a,b,c,d,intPoint);
                if ( (result > NO_INTERSECTION) & (result < X_OVERLAP) ) {
                    intPoints.add(intPoint);
                }
            }
        }

        return intPoints;
    }

    //--- BOOLEAN OPERATION FUNCTIONS ---

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
            //TO-DO: fix XOR once FGroup functionality is ready
                result = trace_or(subjNodes, objNodes, obj, subj);
                break;
        }

        drawNodes(subjNodes, app, 2.0f);
        drawNodes(objNodes, app, 1.0f);

        return result;
    }

    private static ArrayList<Node> initNodes(FPolygon poly){
        ArrayList<Node> nodes = new ArrayList<Node>();
        
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
        for(Node objStart : objNodes){
            for(Node subjStart : subjNodes){
                Node a = subjStart;
                Node b = a.next;
                do{
                    checkIntersections(a, b, objStart, objStart);

                    // increment segment
                    a = b;
                    b = b.next;
                } while( a != subjStart );
            }
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
                    if (d != objStart){
                        checkIntersections(a, subjInt, objStart, d);
                        checkIntersections(subjInt, b, objStart, d);
                    }
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
                    if (d != objStart){
                        checkIntersections(a, subjInt, objStart, d);
                        checkIntersections(subjInt, b, objStart, d);
                    }
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
                    if (d != objStart){
                        checkIntersections(a, subjInt, objStart, d);
                        checkIntersections(subjInt, b, objStart, d);
                    }
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
                    if (d != objStart){
                        checkIntersections(a, subjInt, objStart, d);
                        checkIntersections(subjInt, b, objStart, d);
                    }
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
        
        // catch zero length segments
        if((ab.mag() == 0.0) || (cd.mag() == 0.0)){
            return -1;
        }

        //based on equation:
        //ab * t + a = cd * u + c , which leads to:
        //(cd.y * (a.x - c.x)) - (cd.x * (a.y - c.y)) = t * ((ab.y * cd.x) - (ab.x * cd.y))
        //(ab.y * (a.x - c.x)) - (ab.x * (a.y - c.y)) = u * ((ab.y * cd.x) - (ab.x * cd.y)) 

        // 2nd half of above calculation
        // tests slopes: parallel if this equals 0
        float t = (ab.y * cd.x) - (ab.x * cd.y);
        float u = (ab.y * cd.x) - (ab.x * cd.y);

        float nearZero = 0.0f + epsilon;
        float nearOne = 1.0f - epsilon;

        // non-parallel cases:
        if( Math.abs(t) > 0 ) {
            t = ((cd.y * (a.x - c.x)) - (cd.x * (a.y - c.y))) / t;  //complete t calculation
            u = ((ab.y * (a.x - c.x)) - (ab.x * (a.y - c.y))) / u;  //complete u calculation

            if( (t > nearZero && t < nearOne) && (u > nearZero && u < nearOne) ) {
                intPoint.set((ab.x * t) + a.x, (ab.y * t) + a.y);
                return X_INTERSECTION;
            }
            else if((Math.abs(t) <= nearZero) && (u > nearZero && u < nearOne)){
                return T1_INTERSECTION;
            }
            else if((t > nearZero && t < nearOne) && (Math.abs(u) <= nearZero)){
                return T2_INTERSECTION;
            }
            else if((Math.abs(t) <= nearZero) && (Math.abs(u) <= nearZero)){
                return V_INTERSECTION;
            }
        }
        else {
            //test for colinearity
            if(colinearTest(a, b, c) == 0.0f && (colinearTest(a, b, d) == 0.0f)){
                // parallel cases:
                // t and u now reflect the position of a and c relative to each other

                // with zero length segments removed, one of these should be valid
                if(ab.x != 0.0f){
                    t = (c.x - a.x)/ab.x;
                } else {
                    t = (c.y - a.y)/ab.y;
                }
                if(cd.x != 0.0f){
                    u = (a.x - c.x)/cd.x;
                } else {
                    u = (a.y - c.y)/cd.y;
                }

                if( (t > nearZero && t < nearOne) && (u > nearZero && u < nearOne) ){
                    return X_OVERLAP;
                }
                else if(( (Math.abs(t) <= nearZero) || (Math.abs(t) >= nearOne) ) && (u > nearZero && u < nearOne)){
                    return T1_OVERLAP;
                }
                else if(( (Math.abs(u) <= nearZero) || (Math.abs(u) >= nearOne) ) && (t > nearZero && t < nearOne)){
                    return T2_OVERLAP;
                }
                else if((Math.abs(t) <= nearZero) && (Math.abs(u) <= nearZero)){
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

    // tests which side of line ab point c is on
    // 0 is on line, positive for one side, negative for the other
    private static float colinearTest(PVector a, PVector b, PVector c){
        //return (a.x * (b.y - c.y)) + (b.x * (c.y - a.y)) + (c.x * (a.y - b.y));
        return ((b.x - a.x) * (c.y - a.y)) - ((b.y - a.y) * (c.x - a.x));
    }

    private static FPolygon trace_and(ArrayList<Node> subjNodes, ArrayList<Node> objNodes, FPolygon obj, FPolygon subj){
        FPolygon result = new FPolygon();

        for(Node subjStart : subjNodes){
            tracePath_and(subjStart, obj, result);
        }
        // handle case in which subj encloses obj path
        for(Node objStart : objNodes){
            trace_addUntracedInteriorPaths(objStart, subj, result);
        }

        return result;
    }

    private static void tracePath_and(Node start, FPolygon otherPoly, FPolygon result){
        Node currentNode = start;

        int crossings = 0;      //track crossings for non-intersecting cases

        //searching for untraced crossings loop
        do{
            if(currentNode.isCrossing){
                // count all crossings
                crossings += 1;

                // continue past Node if already traced
                if(currentNode.isTraced()){
                    currentNode = currentNode.next;
                    continue;
                }

                // if Node is an untraced crossing, begin a new path
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
                    //tracingNode = tracingNode.prev;
                    // jump to cross instead of reversing, maintain directionality
                    tracingNode = tracingNode.cross.next;
                }

                // tracing loop
                do{
                    path.appendVertex(tracingNode.pos);
                    tracingNode.trace();

                    if(tracingNode.isCrossing){
                        // determine next direction at crossing from entry/exit flag
                        tracingNode = tracingNode.cross;
                        if(tracingNode.isEntry){
                            moveToNext = true;
                        } else {
                            moveToNext = false;
                        }
                        if(moveToNext){
                            tracingNode = tracingNode.next;
                        } else {
                            //tracingNode = tracingNode.prev;
                            // jump to cross instead of reversing, maintain directionality
                            tracingNode = tracingNode.cross.next;
                        }
                    } else {
                        // continue forward if not a crossing
                        tracingNode = tracingNode.next;
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
                    if(tracingNode.isIntersection()){
                        tracingNode.cross.trace();
                    }
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
            if(currentNode.isCrossing){
                // count all crossings
                crossings += 1;

                // continue past Node if already traced
                if(currentNode.isTraced()){
                    currentNode = currentNode.next;
                    continue;
                }

                // if Node is an untraced crossing, begin a new path
                FPath path = new FPath();

                Node tracingNode = currentNode;
                path.appendVertex(tracingNode.pos);
                tracingNode.trace();

                boolean moveToNext;         //True for traverse forward, False for traverse backwards

                // for OR, avoid entry points
                // if a Node is an entry point -> traverse backwards, else -> forwards
                if(tracingNode.isEntry){
                    moveToNext = false;
                } else {
                    moveToNext = true;
                }

                if(moveToNext){
                    tracingNode = tracingNode.next;
                } else {
                    //tracingNode = tracingNode.prev;
                    //jump to cross instead of reversing, maintain directionality
                    tracingNode = tracingNode.cross.next;
                }

                do{
                    path.appendVertex(tracingNode.pos);
                    tracingNode.trace();

                    if(tracingNode.isCrossing){
                        // determine next direction at crossing from entry/exit flag
                        tracingNode = tracingNode.cross;
                        if(tracingNode.isEntry){
                            moveToNext = false;
                        } else {
                            moveToNext = true;
                        }
                        if(moveToNext){
                            tracingNode = tracingNode.next;
                        } else {
                            // tracingNode = tracingNode.prev;
                            // jump to cross instead of reversing, maintain directionality
                            tracingNode = tracingNode.cross.next;
                        }
                    } else {
                        // continue forward if not a crossing
                        tracingNode = tracingNode.next;
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
                    if(tracingNode.isIntersection()){
                        tracingNode.cross.trace();
                    }
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
        // handle case in which subj encloses obj path
        for(Node objStart : objNodes){
            // reverse any obj paths added this way
            trace_addUntracedInteriorPaths(objStart, subj, result, true);
        }

        return result;
    }

    private static void tracePath_not(Node start, FPolygon otherPoly, FPolygon result){
        Node currentNode = start;

        int crossings = 0;      //track crossing intersections

        do{
            if(currentNode.isCrossing){
                // count all crossings
                crossings += 1;

                // continue past Node if already traced
                if(currentNode.isTraced()){
                    currentNode = currentNode.next;
                    continue;
                }

                // if Node is an untraced crossing, begin a new path
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
                    //tracingNode = tracingNode.prev;
                    //cross instead of reversing, maintain directionality
                    tracingNode = tracingNode.cross.prev;
                    onSubj = !onSubj;
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

    private static void trace_addUntracedInteriorPaths(Node start, FPolygon otherPoly, FPolygon result){
        trace_addUntracedInteriorPaths(start, otherPoly, result, false);
    }

    private static void trace_addUntracedInteriorPaths(Node start, FPolygon otherPoly, FPolygon result, boolean reverse){
        Node currentNode = start;
        // confirm all nodes in path are untraced
        do{
            if(currentNode.isTraced()){
                return;
            }
            currentNode = currentNode.next;
        } while (currentNode != start);

        // find acceptable midpoint to test interiority
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
                // path is inside otherPoly
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

                if(reverse){
                    path.reverse();
                }
                result.addContour(path);

            } else {
                // path is entirely outside otherPoly, do nothing
            }
        } else {
            // interiority unconfirmed, should be identical polygons
            // add result, to complete NOT case
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

            if(reverse){
                path.reverse();
            }
            result.addContour(path);
        }
    }

    // for debug visualization
    private static void drawNodes(ArrayList<Node> nodes, PApplet app, float multiplier){
        app.pushStyle();
        app.noFill();
        for(Node n : nodes){
            Node temp = n;
            do{
                if(temp.isIntersection()){
                    app.strokeWeight(4);

                    // shows sidedness
                    // switch(temp.sidedness){
                    //     case Node.ON_ON:
                    //         app.stroke(0xff537895); //dark blue
                    //         break;
                    //     case Node.ON_RIGHT:
                    //         app.stroke(0xff86a0b4); //mid blue
                    //         break;
                    //     case Node.ON_LEFT:
                    //         app.stroke(0xffbac9d4); //light blue
                    //         break;
                    //     case Node.RIGHT_ON:
                    //         app.stroke(0xffb20035); //dark red
                    //         break;
                    //     case Node.RIGHT_RIGHT:
                    //         app.stroke(0xffc94c71); //mid red
                    //         break;
                    //     case Node.RIGHT_LEFT:
                    //         app.stroke(0xffe099ae); //light red
                    //         break;
                    //     case Node.LEFT_ON:
                    //         app.stroke(0xff8c3880); //dark purple
                    //         break;
                    //     case Node.LEFT_RIGHT:
                    //         app.stroke(0xffae73a6); //mid purple
                    //         break;
                    //     case Node.LEFT_LEFT:
                    //         app.stroke(0xffd1afcc); //light purple
                    //         break;
                    //     case -1:
                    //         app.stroke(0xff00ff00); //neon green
                    //         break;
                    //     default:
                    //         app.stroke(0xff3CD10E); //green
                    //         break;
                    // }
                    
                    // shows entry/exit
                    if(temp.isCrossing){
                        if(temp.isEntry){
                            app.stroke(0xffD10E3C); //red
                        } else {
                            app.stroke(0xff58aed1); //blue
                        }
                    } else {
                        app.stroke(0xff65285d); //purple
                    }
                } else {
                    app.strokeWeight(2);
                    app.stroke(0xff3CD10E); //green
                }
                app.ellipse(temp.pos.x, temp.pos.y, 12.0f*multiplier, 12.0f*multiplier);
                temp = temp.next;
            } while(temp != n);
        }
        app.popStyle();
    }

}