/**
 * ControlPanel -- GUI Control panel with GLCanvas inside it
 *
 * 10/01/13 rdb, derived from RadioSlider lab of CS416
 *
 */

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class ControlPanel extends JFrame

{
    //---------------- class variables ------------------------------  
    private static ControlPanel instance = null;

    //--------------- instance variables ----------------------------
    JPanel drawPanel = null; // this will be used for the rendering area
    JLabel sceneLabel = null;

    JPanel bPanel = null;

    JSlider xSlider, ySlider, zSlider, rSlider, gSlider, bSlider;
    SceneManager sceneM = null;
    //------------------- constructor -------------------------------

    /**
     * return singleton instance of ControlPanel
     */
    public static ControlPanel getInstance() {
        if ( instance == null )
            instance = new ControlPanel();
        return instance;
    }

    /**
     * Constructor is private so can implement the Singleton pattern
     */
    private ControlPanel() {
        super( "ControlPanel" );
        this.setLayout( new GridLayout( 0, 1 ) );
        buildGUI();
        this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        this.pack();
        this.setVisible( true );
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
    //--------------- setSceneTitle( String ) ----------------------

    /**
     * Update the scene title label.
     */
    public void setSceneTitle( String title ) {
        sceneLabel.setText( title );
    }

    //--------------- build GUI components --------------------

    /**
     * Create all the components
     */
    private void buildGUI() {
        // build the button menu
        sceneLabel = new JLabel( "Scene label" );
        this.add( sceneLabel );

        buildButtons();

        this.add( bPanel, BorderLayout.NORTH );
        // build sliders
        buildRadio();
        buildXYZSliders();


        buildCheckBoxes();
        buildLightRadio();
        buildRGBSliders();

    }

    //--------------------- buildButtons ------------------------------------

    /**
     * build a button panel: a Next button and an DrawAxis CheckBox
     */
    private void buildButtons() {

        bPanel = new JPanel();
        bPanel.setBorder( new LineBorder( Color.BLACK, 2 ) );


        //---------------prev button -------------------------------------
        JButton prevButton = new JButton( "Previous Scene" );
        prevButton.addActionListener( new ActionListener() {
                                          public void actionPerformed( ActionEvent ae ) {
                                              SceneManager.prevScene();
                                          }
                                      }
        );

        bPanel.add( prevButton );


        //-------------next button-----------------------------------------
        JButton nextButton = new JButton( "Next Scene" );
        nextButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        SceneManager.nextScene();
                    }
                }
        );
        bPanel.add( nextButton );


        //----------------show axis check box--------------------------------
        JCheckBox axisChecker = new JCheckBox( "Show Axis" );
        axisChecker.setSelected( true );
        axisChecker.addActionListener(

                new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        SceneManager.setDrawAxes
                                ( ( ( JCheckBox ) ae.getSource() ).isSelected() );
                    }
                }
        );
        bPanel.add( axisChecker );


        // this.add( bPanel, BorderLayout.NORTH );
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
     * East region will have a slider controlling the siz e
     */
    private void buildXYZSliders() {

        //------------- X Slider  ------------------------------------
        JPanel xSlidePanel = new JPanel();
        xSlidePanel.setLayout( new FlowLayout() );

        JLabel xLabel = new JLabel( "X slider", JLabel.CENTER );
        xLabel.setAlignmentX( Component.CENTER_ALIGNMENT );
        xLabel.setPreferredSize( new Dimension( 50, 50 ) );

        xSlidePanel.add( xLabel );

        xSlider = new JSlider( JSlider.HORIZONTAL, -360, 360, 0 );
        addLabels( xSlider, 100 );
        xSlider.addChangeListener( new SliderListener( xSlider, "x" ) );
        xSlider.setBorder( new LineBorder( Color.BLACK, 2 ) );
        xSlider.setPreferredSize( new Dimension( 500, 50 ) );

        xSlidePanel.add( xSlider, BorderLayout.SOUTH );
        this.add( xSlidePanel );

        //------------- Y Slider  ------------------------------------

        JPanel ySlidePanel = new JPanel();
        ySlidePanel.setLayout( new FlowLayout() );

        JLabel yLabel = new JLabel( "Y slider", JLabel.CENTER );
        yLabel.setAlignmentX( Component.CENTER_ALIGNMENT );
        yLabel.setPreferredSize( new Dimension( 50, 50 ) );

        ySlidePanel.add( yLabel );

        ySlider = new JSlider( JSlider.HORIZONTAL, -360, 360, 0 );
        // puts min slider value at top
        addLabels( ySlider, 100 );
        ySlider.addChangeListener( new SliderListener( ySlider, "y" ) );
        ySlider.setPreferredSize( new Dimension( 500, 50 ) );
        ySlider.setBorder( new LineBorder( Color.BLACK, 2 ) );

        ySlidePanel.add( ySlider, BorderLayout.SOUTH );
        this.add( ySlidePanel );

        //-----------zSlider----------------------------------------

        JPanel zSlidePanel = new JPanel();
        zSlidePanel.setLayout( new FlowLayout() );

        JLabel zLabel = new JLabel( "Z slider", JLabel.CENTER );
        zLabel.setAlignmentX( Component.CENTER_ALIGNMENT );
        zLabel.setPreferredSize( new Dimension( 50, 50 ) );

        zSlidePanel.add( zLabel );

        zSlider = new JSlider( JSlider.HORIZONTAL, -360, 360, 0 );
        addLabels( zSlider, 100 );
        zSlider.addChangeListener( new SliderListener( zSlider, "z" ) );
        zSlider.setPreferredSize( new Dimension( 500, 50 ) );
        zSlider.setBorder( new LineBorder( Color.BLACK, 2 ) );

        zSlidePanel.add( zSlider, BorderLayout.SOUTH );
        this.add( zSlidePanel );
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
        JPanel buPanel = new JPanel(); // defaults to FlowLayout

        //bPanel.setBorder( new LineBorder( Color.BLACK, 2 ));

        String[] labels = { "Translate", "Rotate Object", "Rotate Scene", "Scale" };


        // for each entry in the labels array, create a JRadioButton
        JRadioButton button = null;
        for ( int i = 0; i < labels.length; i++ ) {
            button = new JRadioButton( labels[i] );
            ButtonListener bListen = new ButtonListener( labels[i] );
            button.addActionListener( bListen );
            bGroup.add( button );
            buPanel.add( button );
        }
        button.setSelected( true );


        this.add( buPanel );
    }
    //++++++++++++++++++++++++ ButtonListener inner class ++++++++++++++++++++++

    /**
     * This version of the innter ButtonListener class constructor has 2 args:
     * the JShape being colored
     * the color to be assigned
     */
    public class ButtonListener implements ActionListener {
        //------ instance variables ---------------
        String doWhat;

        public ButtonListener( String id ) {
            // save the parameter as instance variable of the inner class
            doWhat = id;
        }

        public void actionPerformed( ActionEvent ev ) {
            // get a reference to the radio button that just got pressed.
            JRadioButton button = ( JRadioButton ) ev.getSource();

            String buttonLabel = button.getText(); // get its text field.
            SceneManager.radioButtonChange( buttonLabel );
            //System.out.println( buttonLabel  + ": Action event.  " );
        }
    }


    //-------------------buildCheckBoxes -----------------------------

    /**
     * builds the checkboxes to change the fill and borders
     */
    public void buildCheckBoxes() {

        JPanel checkBoxes = new JPanel();

        JCheckBox pointLight = new JCheckBox( "Point Light" );
        pointLight.setSelected( true );
        CheckBoxListener cbListen = new CheckBoxListener();
        pointLight.addItemListener( cbListen );
        checkBoxes.add( pointLight );

        JCheckBox directionalLight = new JCheckBox( "Directional Light" );
        directionalLight.setSelected( true );
        CheckBoxListener cbListen2 = new CheckBoxListener();
        directionalLight.addItemListener( cbListen2 );
        checkBoxes.add( directionalLight );

        this.add( checkBoxes );

    }
//++++++++++++++++++++++++ CheckBoxListener inner class ++++++++++++++++++++++

    /**
     * This version of the inner CheckBoxListener class constructor
     */
    public class CheckBoxListener implements ItemListener {


        public CheckBoxListener() {

        }

        public void itemStateChanged( ItemEvent e ) {
            // get a reference to the radio button that just got pressed.
            JCheckBox button = ( JCheckBox ) e.getSource();

            String buttonLabel = button.getText(); // get its text field.
            if ( buttonLabel.equals( "Point Light" ) ) {
                if ( e.getStateChange() == ItemEvent.DESELECTED )
                    SceneManager.lightChange( 1, false );
                else if ( e.getStateChange() == ItemEvent.SELECTED )
                    SceneManager.lightChange( 1, true );
            } else if ( buttonLabel.equals( "Directional Light" ) ) {
                if ( e.getStateChange() == ItemEvent.DESELECTED )
                    SceneManager.lightChange( 2, false );
                else if ( e.getStateChange() == ItemEvent.SELECTED )
                    SceneManager.lightChange( 2, true );
            }


        }

    }


    /**
     * Create 3 sliders and add using border layout:
     * First argument is the containing JFrame into which the sliders
     * will be placed
     * 2nd argument is a reference to the JShape that is being controlled.
     * <p/>
     * West region will have a vertical slider controlling the y position
     * South region will have the slider controlling the x position
     * East region will have a slider controlling the siz e
     */
    private void buildRGBSliders() {

        JPanel radioButtonLightPanel = new JPanel();


        //------------- Red Slider  ------------------------------------
        JPanel rSlidePanel = new JPanel();
        rSlidePanel.setLayout( new FlowLayout() );

        JLabel rLabel = new JLabel( "Red", JLabel.CENTER );
        rLabel.setAlignmentX( Component.CENTER_ALIGNMENT );
        rLabel.setPreferredSize( new Dimension( 50, 50 ) );

        rSlidePanel.add( rLabel );

        rSlider = new JSlider( JSlider.HORIZONTAL, 0, 255, 255 );
        addLabels( rSlider, 25 );
        rSlider.addChangeListener( new SliderColorListener( rSlider, "r" ) );
        rSlider.setBorder( new LineBorder( Color.BLACK, 2 ) );
        rSlider.setPreferredSize( new Dimension( 500, 50 ) );

        rSlidePanel.add( rSlider, BorderLayout.SOUTH );
        this.add( rSlidePanel );

        //------------- Green Slider  ------------------------------------

        JPanel gSlidePanel = new JPanel();
        gSlidePanel.setLayout( new FlowLayout() );

        JLabel gLabel = new JLabel( "Green", JLabel.CENTER );
        gLabel.setAlignmentX( Component.CENTER_ALIGNMENT );
        gLabel.setPreferredSize( new Dimension( 50, 50 ) );

        gSlidePanel.add( gLabel );

        gSlider = new JSlider( JSlider.HORIZONTAL, 0, 255, 255 );
        // puts min slider value at top
        addLabels( gSlider, 25 );
        gSlider.addChangeListener( new SliderColorListener( gSlider, "g" ) );
        gSlider.setPreferredSize( new Dimension( 500, 50 ) );
        gSlider.setBorder( new LineBorder( Color.BLACK, 2 ) );

        gSlidePanel.add( gSlider, BorderLayout.SOUTH );
        this.add( gSlidePanel );

        //-----------Blue Slider----------------------------------------

        JPanel bSlidePanel = new JPanel();
        bSlidePanel.setLayout( new FlowLayout() );

        JLabel bLabel = new JLabel( "Blue", JLabel.CENTER );
        bLabel.setAlignmentX( Component.CENTER_ALIGNMENT );
        bLabel.setPreferredSize( new Dimension( 50, 50 ) );

        bSlidePanel.add( bLabel );

        bSlider = new JSlider( JSlider.HORIZONTAL, 0, 255, 255 );
        addLabels( bSlider, 25 );
        bSlider.addChangeListener( new SliderColorListener( bSlider, "b" ) );
        bSlider.setPreferredSize( new Dimension( 500, 50 ) );
        bSlider.setBorder( new LineBorder( Color.BLACK, 2 ) );

        bSlidePanel.add( bSlider, BorderLayout.SOUTH );
        this.add( bSlidePanel );


    }

    //++++++++++++++++++++++++ SliderListener inner class ++++++++++++++++++++++

    /**
     * The SliderListener needs access to
     * -- the slider it is associated with (to get that slider's value)
     * -- the JShape that is being controlled.
     * -- a string that serves as an identifier for the slider
     * These are passed to the constructor.
     */
    public class SliderColorListener implements ChangeListener {
        private JSlider _slider;
        private String _id;

        public SliderColorListener( JSlider slider, String id ) {
            _slider = slider;
            _id = id;
        }
        //------------------- stateChanged -----------------------------

        /**
         * Invoked whenever user changes the state of a slider
         */
        public void stateChanged( ChangeEvent ev ) {

            SceneManager.changeLightColorEvent( _id, _slider.getValue() );
        }
    }


    //--------------------- buildRadio ------------------------------------

    /**
     * build a radio button panel with exclusive behavior (1 button pressed
     * at a time.
     */
    private void buildLightRadio() {
        // The ButtonGroup defines a set of RadioButtons that must be "exclusive"
        //    -- only 1 can be "active" at a time.

        ButtonGroup bGroup = new ButtonGroup();
        JPanel buPanel = new JPanel(); // defaults to FlowLayout

        JRadioButton pointButton = new JRadioButton( "Point Light Color" );
        LightButtonListener pbListen = new LightButtonListener( "Point Light Color" );
        pointButton.addActionListener( pbListen );
        pointButton.setSelected( true );
        bGroup.add( pointButton );
        buPanel.add( pointButton );

        JRadioButton directButton = new JRadioButton( "Directional Light Color" );
        LightButtonListener dbListen = new LightButtonListener( "Directional Light Color" );
        directButton.addActionListener( dbListen );
        bGroup.add( directButton );
        buPanel.add( directButton );

        this.add( buPanel );
    }
    //++++++++++++++++++++++++ ButtonListener inner class ++++++++++++++++++++++

    /**
     * This version of the innter ButtonListener class constructor has 2 args:
     * the JShape being colored
     * the color to be assigned
     */
    public class LightButtonListener implements ActionListener {
        //------ instance variables ---------------
        String doWhat;

        public LightButtonListener( String id ) {
            // save the parameter as instance variable of the inner class
            doWhat = id;
        }

        public void actionPerformed( ActionEvent ev ) {
            // get a reference to the radio button that just got pressed.
            JRadioButton button = ( JRadioButton ) ev.getSource();

            String buttonLabel = button.getText(); // get its text field.
            SceneManager.lightRadioButtonChange( buttonLabel );

        }
    }

}

