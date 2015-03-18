import javax.media.opengl.GL;
import javax.media.opengl.GL2;

/**
 * MaterialProperties.java
 *
 * Defines the way light bounces off an object to make an object appear
 * to have a certain material property
 *
 * @author tml62
 */

public class MaterialProperties {

    float ambRGBA[] = null;
    float specRGBA[] = null;
    float diffRGBA[] = null;
    float shiny;

    float diffRGBADefault[] = new float[]{ 0.8f, 0.8f, 0.8f, 1f };
    float ambRGBADefault[] = new float[]{ 0.2f, 0.2f, 0.2f, 1f };
    float specRGBADefault[] = new float[]{ 0f, 0f, 0f, 1f };
    float shinyDefault = 0f;

    /**
     * constructor for the material property class
     */
    public MaterialProperties() {

        ambRGBA = ambRGBADefault;
        specRGBA = specRGBADefault;
        diffRGBA = diffRGBADefault;
        shiny = 0f;
    }
//-------------------------setMatProp--------------------------------

    /**
     * sets the material properties
     */
    public void setMatProp() {

        JOGL.gl.glDisable( GL2.GL_COLOR_MATERIAL );
        JOGL.gl.glMaterialfv( GL2.GL_FRONT, GL2.GL_DIFFUSE, diffRGBA, 0 );
        JOGL.gl.glMaterialfv( GL2.GL_FRONT, GL2.GL_AMBIENT, ambRGBA, 0 );
        JOGL.gl.glMaterialfv( GL2.GL_FRONT, GL2.GL_SPECULAR, specRGBA, 0 );

        JOGL.gl.glMaterialf( GL2.GL_FRONT, GL2.GL_SHININESS, shiny );
        //JOGL.gl.glEnable(GL2.GL_COLOR_MATERIAL);

    }
//------------------clearMatProp----------------------

    /**
     * clear the material properties and sets them back to gl defaults
     */
    public void clearMatProp() {
        //JOGL.gl.glDisable(GL2.GL_COLOR_MATERIAL);
        JOGL.gl.glMaterialfv( GL2.GL_FRONT, GL2.GL_DIFFUSE, diffRGBADefault, 0 );
        JOGL.gl.glMaterialfv( GL2.GL_FRONT, GL2.GL_AMBIENT, ambRGBADefault, 0 );
        JOGL.gl.glMaterialfv( GL2.GL_FRONT, GL2.GL_SPECULAR, specRGBADefault, 0 );

        JOGL.gl.glMaterialf( GL2.GL_FRONT, GL2.GL_SHININESS, shinyDefault );

        JOGL.gl.glEnable( GL2.GL_COLOR_MATERIAL );
    }
//-----------------setAmbient----------------------------

    /**
     * sets the ambient light values for the material
     */
    public void setAmbient( float[] amb ) {
        if ( amb.length != 4 )
            System.err.println( "Wrong number of parameters for ambient reflection!" );
        else {
            ambRGBA = amb;
        }
    }
//-----------------setSpecular----------------------------

    /**
     * sets the specular light values for the material
     */
    public void setSpecular( float[] spec ) {
        if ( spec.length != 4 )
            System.err.println( "Wrong number of parameters for specular reflection!" );
        else {
            ambRGBA = spec;
        }
    }

//-----------------setShiny----------------------------

    /**
     * sets the shiniess value for the material
     */
    public void setShiny( float shine ) {
        shiny = shine;
    }

//-----------------setDiffuse----------------------------

    /**
     * sets the diffuse light values for the material
     */
    public void setDiffuse( float[] diff ) {
        if ( diff.length != 4 )
            System.err.println( "Wrong number of parameters for ambient reflection!" );
        else {
            diffRGBA = diff;
        }
    }

}
