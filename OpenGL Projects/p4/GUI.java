/**
 * GUI -- GUI Control panel with GLCanvas inside it
 *
 * 10/01/13 rdb, derived from RadioSlider lab of CS416
 *
 */

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class GUI extends JPanel {
    //---------------- class variables ------------------------------  
    private static GUI instance = null;

    //--------------- instance variables ----------------------------
    JPanel drawPanel = null; // this will be used for the rendering area

    //------------------- constructor -------------------------------

    /**
     * return singleton instance of GUI
     */
    public static GUI getInstance() {
        if ( instance == null )
            instance = new GUI();
        return instance;
    }

    /**
     * Constructor is private so can implement the Singleton pattern
     */
    private GUI() {
        this.setLayout( new BorderLayout() );
        buildGUI();
    }
    //--------------- addDrawPanel() -----------------------------

    /**
     * add component to draw panel
     */
    public void addDrawPanel( Component drawArea ) {
        this.add( drawArea, BorderLayout.CENTER );
    }
    //--------------- getDrawPanel() -----------------------------

    /**
     * return reference to the drawing area
     */
    public JPanel getDrawPanel() {
        return drawPanel;
    }
    //--------------- nextScene() -------------------------------

    /**
     * Go to the next scene
     */
    public void nextScene() {
        System.out.println( "Next Scene" );
    }
    //--------------- build GUI components --------------------

    /**
     * Create all the components
     */
    private void buildGUI() {
        // build the button menu
        buildButtons();

        // build sliders
        buildSliders();
    }

    //--------------------- buildButtons ------------------------------------

    /**
     * build a button panel: a Next button and and CheckBoxes
     */
    private void buildButtons() {
        //-------------- Next button ---------------------------------
        JPanel bPanel = new JPanel();
        bPanel.setBorder( new LineBorder( Color.BLACK, 2 ) );

        JButton nextButton = new JButton( "Next Scene" );
        nextButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        SceneManager.nextScene();
                    }
                }
        );
        bPanel.add( nextButton );

        // add check boxes to this panel
        buildCheckBoxes( bPanel );

        this.add( bPanel, BorderLayout.NORTH );
    }

    //--------------------- buildCheckboxes --------------------------

    /**
     * build a button panel: a Next button and CheckBoxes
     */
    private void buildCheckBoxes( JPanel panel ) {
        //----------------- Show Axis CheckBox ------------------------
        JCheckBox axisChecker = new JCheckBox( "Use Normals" );
        axisChecker.setSelected( true );
        axisChecker.addActionListener(
                new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        SceneManager.setDrawAxes
                                ( ( ( JCheckBox ) ae.getSource() ).isSelected() );
                    }
                }
        );
        panel.add( axisChecker );

        //--------------- Light 0 CheckBox -----------------------------
        JCheckBox light0Checker = new JCheckBox( "Light 0" );
        light0Checker.setSelected( true );
        light0Checker.addActionListener(
                new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        SceneManager.setLight0
                                ( ( ( JCheckBox ) ae.getSource() ).isSelected() );
                    }
                }
        );
        panel.add( light0Checker );

        //--------------- GLSL CheckBox -----------------------------
        JCheckBox glslChecker = new JCheckBox( "GLSL" );
        glslChecker.setSelected( false );
        glslChecker.addActionListener(
                new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        SceneManager.setGLSL
                                ( ( ( JCheckBox ) ae.getSource() ).isSelected() );
                    }
                }
        );
        panel.add( glslChecker );
    }
    //------------------- buildSliders ---------------------------------

    /**
     * Create 3 sliders and add using border layout:
     * First argument is the containing JFrame into which the sliders
     * will be placed
     * 2nd argument is a reference to the JShape that is being controlled.
     * <p/>
     * West region will have a vertical slider controlling the y position
     * South region will have the slider controlling the x position
     * East region will have a slider controlling the light color
     */
    private void buildSliders() {
        //------------- X Slider  ------------------------------------
        JSlider xSlider = new JSlider( JSlider.HORIZONTAL, 0, 500, 250 );
        addLabels( xSlider, 100 );
        xSlider.addChangeListener( new SliderListener( xSlider, "x" ) );
        xSlider.setBorder( new LineBorder( Color.BLACK, 2 ) );
        this.add( xSlider, BorderLayout.SOUTH );

        //------------- Y Slider  ------------------------------------
        JSlider ySlider = new JSlider( JSlider.VERTICAL, 0, 500, 250 );
        ySlider.setInverted( true );    // puts min slider value at top 
        addLabels( ySlider, 100 );
        ySlider.addChangeListener( new SliderListener( ySlider, "y" ) );
        ySlider.setBorder( new LineBorder( Color.BLACK, 2 ) );
        this.add( ySlider, BorderLayout.WEST );

        //------------- L (lightc color) Slider  -----------------------
        JSlider lSlider = new JSlider( JSlider.VERTICAL, 0, 500, 250 );
        addLabels( lSlider, 10 );
        lSlider.addChangeListener( new SliderListener( lSlider, "z" ) );
        lSlider.setBorder( new LineBorder( Color.BLACK, 2 ) );
        this.add( lSlider, BorderLayout.EAST );

    }
    //---------------- addLabels( JSlider, int ) -----------------------

    /**
     * a utility method to add tick marks.
     * First argument is the slider, the second represents the
     * major tick mark interval
     * minor tick mark interval will be 1/10 of that.
     */
    private void addLabels( JSlider slider, int majorTicks ) {
        slider.setPaintTicks( true );
        slider.setPaintLabels( true );
        slider.setMajorTickSpacing( majorTicks );
        slider.setMinorTickSpacing( majorTicks / 10 );
    }
    //++++++++++++++++++++++++ SliderListener inner class ++++++++++++++++++++++

    /**
     * The SliderListener needs access to
     * -- the slider it is associated with (to get that slider's value)
     * -- the JShape that is being controlled.
     * -- a string that serves as an identifier for the slider
     * These are passed to the constructor.
     */
    public class SliderListener implements ChangeListener {
        private JSlider _slider;
        private String _id;

        public SliderListener( JSlider slider, String id ) {
            _slider = slider;
            _id = id;
        }
        //------------------- stateChanged -----------------------------

        /**
         * Invoked whenever user changes the state of a slider
         */
        public void stateChanged( ChangeEvent ev ) {


            //+++++++++ this is a very poor "hack": just a crude way
            //  of getting event to show up in the display of this demo
            //  A real application needs a cleaner set of interfaces between
            //  the Listener object and the Responder object (in the
            //  Source/Listener/Responder interaction model).
            SceneManager.changeEvent( _id, _slider.getValue() );
        }
    }
    //--------------------- buildRadio ------------------------------------

    /**
     * build a radio button panel with exclusive behavior (1 button pressed
     * at a time.
     */
    private void buildRadio() {
        // The ButtonGroup defines a set of RadioButtons that must be "exclusive"
        //    -- only 1 can be "active" at a time.

        ButtonGroup bGroup = new ButtonGroup();
        JPanel bPanel = new JPanel(); // defaults to FlowLayout

        bPanel.setBorder( new LineBorder( Color.BLACK, 2 ) );

        String[] labels = { "Blue", "Red", "Green", "Cyan",
                "Magenta", "Black", "Orange" };
        Color[] colors = { Color.BLUE, Color.RED, Color.GREEN, Color.CYAN,
                Color.MAGENTA, Color.BLACK, Color.ORANGE };

        // for each entry in the labels array, create a JRadioButton
        JRadioButton button = null;
        for ( int i = 0; i < labels.length; i++ ) {
            button = new JRadioButton( labels[ i ] );
            ButtonListener bListen = new ButtonListener( colors[ i ] );
            button.addActionListener( bListen );
            bGroup.add( button );
            bPanel.add( button );
        }
        button.setSelected( true );

        this.add( bPanel, BorderLayout.NORTH );
    }
    //++++++++++++++++++++++++ ButtonListener inner class ++++++++++++++++++++++

    /**
     * This version of the innter ButtonListener class constructor has 2 args:
     * the JShape being colored
     * the color to be assigned
     */
    public class ButtonListener implements ActionListener {
        //------ instance variables ---------------
        Color _color;

        public ButtonListener( Color color ) {
            // save the parameter as instance variable of the inner class
            _color = color;
        }

        public void actionPerformed( ActionEvent ev ) {
            // get a reference to the radio button that just got pressed.
            JRadioButton button = ( JRadioButton ) ev.getSource();

            String buttonLabel = button.getText(); // get its text field.
            System.out.println( buttonLabel + ": Action event.  " );
        }
    }
}
