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
    //--------------- build GUI components --------------------
    /**
     *  Create all the components
     */
    private void buildGUI()
    {        
        // build the sliders to control position and size
        buildSliders();
        
        // build the button menu
        buildButtons();
        // build the radio button panel to change the color
        //buildRadio();
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
     *   East region will have a slider controlling the size
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
        JSlider xSlider = new JSlider( JSlider.HORIZONTAL, 0, 500, 250 );
        addLabels( xSlider, 100 );
        xSlider.addChangeListener( new SliderListener( xSlider, "x" ));
        xSlider.setBorder( new LineBorder( Color.BLACK, 2 ));
        this.add( xSlider, BorderLayout.SOUTH );
        
        //------------- Y Slider  ------------------------------------
        JSlider ySlider = new JSlider( JSlider.VERTICAL, 0, 500, 250 );
        ySlider.setInverted( true );    // puts min slider value at top 
        addLabels( ySlider, 100 );
        ySlider.addChangeListener( new SliderListener( ySlider, "y" ));
        ySlider.setBorder( new LineBorder( Color.BLACK, 2 ));
        this.add( ySlider, BorderLayout.WEST );
        
        //------------- S (size) Slider  ------------------------------------
        // 2. Copy and edit again to create the Size slider for controlling 
        //    size of the target JShape.
        //    Add code to SliderListener to process S-slider events.
        //////////////////////////////////////////////////////////////////
        //------------- Size Slider  ------------------------------------
        JSlider sSlider = new JSlider( JSlider.VERTICAL, 1, 10, 1 );
        addLabels( sSlider, 100 );
        sSlider.addChangeListener( new SliderListener( sSlider, "Size" ));
        sSlider.setBorder( new LineBorder( Color.BLACK, 2 ));
        this.add( sSlider, BorderLayout.EAST );
        
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
            
            if ( _id.equals( "y" ))       // y-slider
                System.out.println( "Y: " + _slider.getValue() );
            else if ( _id.equals( "x" ))  // x-slider
                System.out.println( "X: " + _slider.getValue() );
            else   // size slider
                System.out.println( "S: " + _slider.getValue() );
            
            //+++++++++ this is a very poor "hack": just a crude way
            //  of getting event to show up in the display of this demo
            //  A real application needs a cleaner set of interfaces between
            //  the Listener object and the Responder object (in the
            //  Source/Listener/Responder interaction model).
            UIjogl.changeEvent( _id, _slider.getValue() );
        }
    }
    
    //--------------------- buildButtons ------------------------------------
    /**
     * build a button panel; for now, only button is Next
     */
    private void buildButtons()
    { 
        JPanel bPanel = new JPanel();
        bPanel.setBorder( new LineBorder( Color.BLACK, 2 ));
        
        JButton nextButton = new JButton( "Next Scene" );
        nextButton.addActionListener(
             new ActionListener()
             {
                  public void actionPerformed( ActionEvent ae ) { nextScene(); } 
             }
         );
        bPanel.add( nextButton );
        this.add( bPanel, BorderLayout.NORTH );
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
        JPanel      bPanel = new JPanel(); // defaults to FlowLayout
        
        bPanel.setBorder( new LineBorder( Color.BLACK, 2 ));
        
        String[]  labels = { "Blue", "Red", "Green", "Cyan", 
            "Magenta", "Black", "Orange" };
        Color[]   colors = { Color.BLUE, Color.RED, Color.GREEN, Color.CYAN, 
            Color.MAGENTA, Color.BLACK, Color.ORANGE };
        
        // for each entry in the labels array, create a JRadioButton
        JRadioButton button = null;
        for ( int i = 0; i < labels.length; i++ )
        {
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
     *   the JShape being colored 
     *   the color to be assigned
     */
    public class ButtonListener implements ActionListener
    {
        //------ instance variables ---------------
        Color  _color;
        
        public ButtonListener( Color color )
        {
            // save the parameter as instance variable of the inner class
            _color  = color;
        }
        public void actionPerformed( ActionEvent ev )
        { 
            // get a reference to the radio button that just got pressed.
            JRadioButton button = (JRadioButton) ev.getSource();
            
            String buttonLabel = button.getText(); // get its text field.
            System.out.println( buttonLabel  + ": Action event.  " ); 
        }
    }
}
