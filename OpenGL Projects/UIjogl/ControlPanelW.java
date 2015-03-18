/**
 * ControlPanelW -- GUI Control panel as separate window
 * 
 * 10/01/13 rdb, derived from RadioSlider lab of CS416
 * 
 */
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class ControlPanelW extends JFrame 
{
    //---------------- class variables ------------------------------  
    private static ControlPanelW instance = null;


     JSlider xSlider,ySlider;
    JTextField shapeToDel;
    JPanel checkBoxes;
    JTextArea currentShape;
    //------------------- constructor -------------------------------
     /**
     * return singleton instance of ControlPanelW
     */
    public static ControlPanelW getInstance()
    {
        if ( instance == null )
            instance = new ControlPanelW();
        return instance;
    }
   /**
     * Constructor is private so can implement the Singleton pattern
     */
    private ControlPanelW()     
    {
        super( "ControlPanel" );
        this.setLayout( new GridLayout( 0, 1 ) );
        buildGUI();
        this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE ); 
        this.pack();
        this.setVisible( true );
    }
    //--------------- build GUI components --------------------
    /**
     *  Create all the components
     */
    private void buildGUI()
    {        
        // build the sliders to control position and size
        buildSliders();
        
        // build the radio button panel to change the color
        buildRadio();

        buildButtons();

        buildCheckBoxes();

        buildLists();
        this.add(checkBoxes);
    }        
    
    public void setxSlider( int val )
    {
        xSlider.setValue( val );
    }
    public void setySlider( int val )
    {
        ySlider.setValue( val );
    }
    public void changeCurrShape(String s)
    {
        currentShape.setText(s);
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
        xSlider = new JSlider( JSlider.HORIZONTAL, 0, 500, 250 );
        addLabels( xSlider, 100 );
        xSlider.addChangeListener( new SliderListener( xSlider, "x" ));
        xSlider.setBorder( new LineBorder( Color.BLACK, 2 ));
        this.add( xSlider, BorderLayout.SOUTH );
        
        //------------- Y Slider  ------------------------------------
        ySlider = new JSlider( JSlider.HORIZONTAL, 0, 500, 250 );
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
        /*
        JSlider sSlider = new JSlider( JSlider.HORIZONTAL, 1, 10, 1 );
        addLabels( sSlider, 100 );
        sSlider.addChangeListener( new SliderListener( sSlider, "Size" ));
        sSlider.setBorder( new LineBorder( Color.BLACK, 2 ));
        this.add( sSlider, BorderLayout.EAST );
        */
        
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
            UIjogl.changeEvent( _id, _slider.getValue() );        }
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
        
        String[]  labels = { "Right Triangle (RT)", "Equal Triangle (ET)", "Rectangle (RE)", "Parallelogram (PG)",
            "Trapezoid (TR)", "Pantagon (FP)", "Octagon (EP)", "Nonagon (NP)" };
        String[]   shapes = { "rtri", "etri", "rect", "pgram", "trap", "fPoly",
            "ePoly", "nonPoly" };
        
        // for each entry in the labels array, create a JRadioButton
        JRadioButton button = null;
        for ( int i = 0; i < labels.length; i++ )
        {
            button = new JRadioButton( labels[ i ] );
            ButtonListener bListen = new ButtonListener( shapes [ i ] );
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
        String  id;
        
        public ButtonListener( String s )
        {
            // save the parameter as instance variable of the inner class
            id = s;
        }
        public void actionPerformed( ActionEvent ev )
        { 
            // get a reference to the radio button that just got pressed.
            JRadioButton button = (JRadioButton) ev.getSource();
            
            String buttonLabel = button.getText(); // get its text field.
            UIjogl.shapeChange(id);

        }

    }
//------------------- buildButtons -----------------------------
    /**
     * Builds the two buttons to delete shapes. Also stores the text field that holds the current shape
     */
    public void buildButtons()
    {
        JPanel delBPanel = new JPanel();
        JButton delAllButton = new JButton("Delete All");
        DelButtonListener bListen = new DelButtonListener( );
        delAllButton.addActionListener(bListen);
        delBPanel.add(delAllButton);

        JButton delButton = new JButton("Delete This:");
        DelButtonListener beListen = new DelButtonListener( );
        delButton.addActionListener(beListen);
        delBPanel.add(delButton);

        shapeToDel = new JTextField();
        shapeToDel.setPreferredSize(new Dimension(100,30));
        delBPanel.add(shapeToDel);

        JLabel textLabel = new JLabel("Current Shape: ");
        delBPanel.add(textLabel);

        currentShape = new JTextArea("NULL");
        delBPanel.add(currentShape);


        this.add(delBPanel);
    }
    //++++++++++++++++++++++++ DelButtonListener inner class ++++++++++++++++++++++
    /**
     * This version of the innter DelButtonListener class constructor
     *
     */
    public class DelButtonListener implements ActionListener
    {


        public DelButtonListener( )
        {

        }
        public void actionPerformed( ActionEvent ev )
        {
            // get a reference to the radio button that just got pressed.
            JButton button = (JButton) ev.getSource();

            String buttonLabel = button.getText(); // get its text field.
            if (buttonLabel.equals("Delete All"))
            {
                UIjogl.deleteAll();
            }
            else
            {
                UIjogl.shapeDelete( shapeToDel.getText() );
            }


        }

    }
//-------------------buildBoxes -----------------------------
    /**
     * builds the checkboxes to change the fill and borders
     */
    public void buildCheckBoxes()
    {

        checkBoxes = new JPanel();
        JCheckBox fillCheck = new JCheckBox("Fill Shape");
        fillCheck.setSelected( true );
        CheckBoxListener cbListen = new CheckBoxListener();
        fillCheck.addItemListener(cbListen);
        checkBoxes.add(fillCheck);

        JCheckBox borderCheck = new JCheckBox("Shape Border");
        CheckBoxListener cbListen2 = new CheckBoxListener();
        borderCheck.addItemListener(cbListen2);
        checkBoxes.add(borderCheck);


    }
//++++++++++++++++++++++++ CheckBoxListener inner class ++++++++++++++++++++++
    /**
     * This version of the innter CheckBoxListener class constructor
     *
     */
    public class CheckBoxListener implements ItemListener
    {


        public CheckBoxListener( )
        {

        }
        public void itemStateChanged( ItemEvent e )
        {
            // get a reference to the radio button that just got pressed.
            JCheckBox button = (JCheckBox) e.getSource();

            String buttonLabel = button.getText(); // get its text field.
            if (buttonLabel.equals("Fill Shape"))
            {
                if (e.getStateChange() == ItemEvent.DESELECTED)
                    UIjogl.fillChange( false );
                else if (e.getStateChange() == ItemEvent.SELECTED)
                    UIjogl.fillChange( true );
            }
            else if ( buttonLabel.equals("Shape Border"))
            {
                if (e.getStateChange() == ItemEvent.DESELECTED)
                    UIjogl.boundChange( false );
                else if (e.getStateChange() == ItemEvent.SELECTED)
                    UIjogl.boundChange(true);
            }


        }

    }
//-------------------buildBoxes -----------------------------
    /**
     * builds the lists that hold the colors for the fill and border color
     */
    public void buildLists()
    {
        JPanel colorPanel = new JPanel();

        DefaultListModel<String> colorList = new DefaultListModel<String>();
        colorList.add(0,"Black");
        colorList.add(1,"Blue");
        colorList.add(2,"Yellow");
        colorList.add(3,"Magenta");
        colorList.add(4,"Cyan");
        colorList.add(5,"Green");

        JLabel fillLabel = new JLabel("Border Colors");
        checkBoxes.add(fillLabel);

        @SuppressWarnings("unchecked")
        JList fillcolorList = new JList(colorList);
        fillcolorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fillcolorList.setLayoutOrientation(JList.VERTICAL);
        FillColorListener cListen = new FillColorListener();
        fillcolorList.addListSelectionListener(cListen);


        checkBoxes.add(fillcolorList);

        JLabel borderLabel = new JLabel("Fill Colors");
        checkBoxes.add(borderLabel);

        @SuppressWarnings("unchecked")
        JList bordercolorList = new JList(colorList);

        bordercolorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bordercolorList.setLayoutOrientation(JList.VERTICAL);
        BorderColorListener cListen2 = new BorderColorListener();
        bordercolorList.addListSelectionListener(cListen2);
        checkBoxes.add(bordercolorList);

        //this.add(colorPanel);

    }
    public class FillColorListener implements ListSelectionListener
    {

        JList temp = new JList();
        public FillColorListener()
        {

        }
        public void valueChanged( ListSelectionEvent e )
        {

            JList source = (JList) e.getSource();


            if (source.getSelectedIndex() == 0)
                UIjogl.borderColorChange(Color.BLACK);
            else if (source.getSelectedIndex() == 1)
                UIjogl.borderColorChange(Color.BLUE);
            else if (source.getSelectedIndex() == 2)
                UIjogl.borderColorChange(Color.YELLOW);
            else if (source.getSelectedIndex() == 3)
                UIjogl.borderColorChange(Color.MAGENTA);
            else if (source.getSelectedIndex() == 4)
                UIjogl.borderColorChange(Color.CYAN);
            else if (source.getSelectedIndex() == 5)
                UIjogl.borderColorChange(Color.GREEN);
            else
                System.err.println("Invalid Fill Color Selection");



        }

    }

    public class BorderColorListener implements ListSelectionListener
    {

        JList temp = new JList();
        public BorderColorListener( )
        {

        }
        public void valueChanged( ListSelectionEvent e )
        {

            JList source = (JList) e.getSource();


            if (source.getSelectedIndex() == 0)
                UIjogl.colorChange( Color.BLACK );
            else if (source.getSelectedIndex() == 1)
                UIjogl.colorChange( Color.BLUE );
            else if (source.getSelectedIndex() == 2)
                UIjogl.colorChange( Color.YELLOW );
            else if (source.getSelectedIndex() == 3)
                UIjogl.colorChange( Color.MAGENTA );
            else if (source.getSelectedIndex() == 4)
                UIjogl.colorChange( Color.CYAN );
            else if (source.getSelectedIndex() == 5)
                UIjogl.colorChange( Color.GREEN );
            else
                System.err.println("Invalid Fill Color Selection");



        }

    }

}
