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

public class ControlPanel extends JPanel 
{
    //---------------- class variables ------------------------------  
    private static ControlPanel instance = null;
    
    //--------------- instance variables ----------------------------
    JPanel drawPanel = null; // this will be used for the rendering area
    JLabel sceneLabel = null;

    JPanel bPanel = null;

    JSlider xSlider,ySlider,zSlider;
    SceneManager sceneM = null;
    //------------------- constructor -------------------------------
     /**
     * return singleton instance of ControlPanel
     */
    public static ControlPanel getInstance()
    {
        if ( instance == null )
            instance = new ControlPanel();
        return instance;
    }
   /**
     * Constructor is private so can implement the Singleton pattern
     */
    private ControlPanel()     
    {
        this.setLayout( new BorderLayout() );
        //sceneM = SceneManager.getInstance();
        buildGUI();
    }
    //--------------- addDrawPanel() -----------------------------
    /**
     * add component to draw panel
     */
    public void addDrawPanel( Component drawArea )
    {
        this.add( drawArea, BorderLayout.CENTER );
    }
    //--------------- getDrawPanel() -----------------------------
    /**
     * return reference to the drawing area
     */
    public JPanel getDrawPanel()
    {
        return drawPanel;
    }
    //--------------- nextScene() -------------------------------
    /**
     * Go to the next scene
     */
    public void nextScene()
    {
        System.out.println( "Next Scene" );
    }
    //--------------- setSceneTitle( String ) ----------------------
    /**
     * Update the scene title label.
     */
    public void setSceneTitle( String title )
    {
        sceneLabel.setText( title );
    }

    //--------------- build GUI components --------------------
    /**
     *  Create all the components
     */
    private void buildGUI()
    {        
        // build the button menu
        buildButtons();
        
        // build sliders
        buildSliders();

        buildRadio();
        this.add(bPanel, BorderLayout.NORTH);
    }        
      
    //--------------------- buildButtons ------------------------------------
    /**
     * build a button panel: a Next button and an DrawAxis CheckBox
     */
    private void buildButtons()
    { 

        bPanel = new JPanel();
        bPanel.setBorder( new LineBorder( Color.BLACK, 2 ));

        sceneLabel = new JLabel( "Scene label" );
        bPanel.add( sceneLabel );

        //---------------prev button -------------------------------------
        JButton prevButton = new JButton("Previous Scene");
        prevButton.addActionListener(new ActionListener()
        {
            public void actionPerformed( ActionEvent ae )
            {
                SceneManager.prevScene();
            }
        }
        );

        bPanel.add(prevButton);


       //-------------next button-----------------------------------------
        JButton nextButton = new JButton( "Next Scene" );
        nextButton.addActionListener(
                new ActionListener()
                {
                    public void actionPerformed( ActionEvent ae )
                    {
                        SceneManager.nextScene();
                    }
                }
        );
        bPanel.add( nextButton );


        //----------------show axis check box--------------------------------
        JCheckBox axisChecker = new JCheckBox( "Show Axis" );
        axisChecker.setSelected( true );
        axisChecker.addActionListener(

             new ActionListener()
             {
                  public void actionPerformed( ActionEvent ae ) 
                  { 
                      SceneManager.setDrawAxes  
                               ( ((JCheckBox) ae.getSource() ).isSelected() ); 
                  } 
             }
         );
        bPanel.add( axisChecker );



       // this.add( bPanel, BorderLayout.NORTH );
    }
    //------------------- buildSliders ---------------------------------
    /**
     * Create 3 sliders and add using border layout:
     *   First argument is the containing JFrame into which the sliders
     *     will be placed
     *   2nd argument is a reference to the JShape that is being controlled.
     * 
     *   West region will have a vertical slider controlling the y position 
     *   South region will have the slider controlling the x position
     *   East region will have a slider controlling the siz e
     */
    private void buildSliders()
    {
        
        //////////////////////////////////////////////////////////////////
        // 1. Copy and edit the above Y slider code to make an X slider
        //    in the SOUTH border region.
        //    Add code to the SliderListener code to process the X-slider events
        // 2. Copy and edit again to create the Size slider for controlling 
        //    size of the target JShape.
        //    Add code to SliderListener to process S-slider events.
        //////////////////////////////////////////////////////////////////
        //------------- X Slider  ------------------------------------
        xSlider = new JSlider( JSlider.HORIZONTAL, -360, 360, 0 );
        addLabels( xSlider, 100 );
        xSlider.addChangeListener( new SliderListener( xSlider, "x" ));
        xSlider.setBorder( new LineBorder( Color.BLACK, 2 ));
        this.add( xSlider, BorderLayout.SOUTH );
        
        //------------- Y Slider  ------------------------------------


        ySlider = new JSlider( JSlider.VERTICAL, -360, 360, 0 );
        ySlider.setInverted( true );    // puts min slider value at top 
        addLabels( ySlider, 100 );
        ySlider.addChangeListener( new SliderListener( ySlider, "y" ));
        ySlider.setBorder( new LineBorder( Color.BLACK, 2 ));
        this.add( ySlider, BorderLayout.WEST );
        

        zSlider = new JSlider( JSlider.VERTICAL, -360, 360, 0 );
        addLabels( zSlider, 100 );
        zSlider.addChangeListener( new SliderListener( zSlider, "z" ));
        zSlider.setBorder( new LineBorder( Color.BLACK, 2 ));
        this.add( zSlider, BorderLayout.EAST );

    }  
    //---------------- addLabels( JSlider, int ) -----------------------
    /**
     * a utility method to add tick marks.
     * First argument is the slider, the second represents the
     *   major tick mark interval
     *   minor tick mark interval will be 1/10 of that.
     */
    private void addLabels( JSlider slider, int majorTicks )
    {
        slider.setPaintTicks( true );
        slider.setPaintLabels( true );
        slider.setMajorTickSpacing( majorTicks );
        slider.setMinorTickSpacing( majorTicks / 10 );
    }
    //++++++++++++++++++++++++ SliderListener inner class ++++++++++++++++++++++
    /**
     * The SliderListener needs access to 
     *   -- the slider it is associated with (to get that slider's value)
     *   -- the JShape that is being controlled.
     *   -- a string that serves as an identifier for the slider
     * These are passed to the constructor.
     */
    public class SliderListener implements ChangeListener
    {
        private JSlider     _slider;
        private String      _id;
        
        public SliderListener( JSlider slider, String id )
        {
            _slider = slider;
            _id     = id;
        }
        //------------------- stateChanged -----------------------------
        /**
         * Invoked whenever user changes the state of a slider
         */
        public void stateChanged( ChangeEvent ev )
        {
            //////////////////////////////////////////////////////////////
            // a. add code to respond to the y-slider. it needs to
            //   change the y-position of the target rectangle
            // b. After adding the x-slider, need to test here which slider
            //    generated the event; can do that by testing the
            //    _id field that was set in the constructor. 
            //    Compare it (using the String equals method) to the 
            //    String used to create this instance of the SliderListener.
            // c. After adding the size slider, need to augment this code
            //    to identify and handle events from that slider.
            /////////////////////////////////////////////////////////////
            /*
            if ( _id.equals( "y" ))       // y-slider
            {
                System.out.println( "Y: " + _slider.getValue() );
                float red = ((float) _slider.getValue()) / 500.0f;
                SceneManager.setLightColor( new Color( red, 0.7f, 0.7f ));
            }
            else if ( _id.equals( "x" ))  // x-slider
                System.out.println( "X: " + _slider.getValue() );
            else   // size slider
                System.out.println( "S: " + _slider.getValue() );
            */
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
     *   at a time.
     */
    private void buildRadio()
    {  
        // The ButtonGroup defines a set of RadioButtons that must be "exclusive"
        //    -- only 1 can be "active" at a time.
        
        ButtonGroup bGroup = new ButtonGroup();
        JPanel      buPanel = new JPanel(); // defaults to FlowLayout
        
        //bPanel.setBorder( new LineBorder( Color.BLACK, 2 ));
        
        String[]  labels = { "T", "R", "S" };

        
        // for each entry in the labels array, create a JRadioButton
        JRadioButton button = null;
        for ( int i = 0; i < labels.length; i++ )
        {
            button = new JRadioButton( labels[ i ] );
            ButtonListener bListen = new ButtonListener( labels[i] );
            button.addActionListener( bListen );
            bGroup.add( button );
            bPanel.add( button );
        }
        button.setSelected( true );
               
        //bPanel.add( buPanel );
    }
    //++++++++++++++++++++++++ ButtonListener inner class ++++++++++++++++++++++
    /**
     * This version of the innter ButtonListener class constructor has 2 args:
     *   the JShape being colored 
     *   the color to be assigned
     */
    public class ButtonListener implements ActionListener
    {
        //------ instance variables ---------------
        String doWhat;
        
        public ButtonListener( String id )
        {
            // save the parameter as instance variable of the inner class
            doWhat  = id;
        }
        public void actionPerformed( ActionEvent ev )
        { 
            // get a reference to the radio button that just got pressed.
            JRadioButton button = (JRadioButton) ev.getSource();
            
            String buttonLabel = button.getText(); // get its text field.
            SceneManager.radioButtonChange(buttonLabel);
            System.out.println( buttonLabel  + ": Action event.  " ); 
        }
    }
}
