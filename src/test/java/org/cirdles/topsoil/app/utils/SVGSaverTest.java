import junit.framework.TestCase;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.easymock.EasyMock.eq;


public class SVGSaverTest extends TestCase{
    private SVGSaver svg;
    private Transformer mockTransformer;

    @Before
    public void setUp(){
    
        svg = new SVGSaver();
        mockTransformer = createStrictMock(Transformer.class)
            .setTransformer(mockTransformer);
    }

    @Test
    public void testWriteSVGToFile(){
        File results = new File();
        String SVG_DOCTYPE_PUBLIC = "-//W3C//DTD SVG 1.1//EN";  
        String SVG_DOCTYPE_SYSTEM = "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd";
          
        expect(mockTransformer.setOutputProperty(eq(SVG_DOCTYPE_PUBLIC), eq(SVG_DOCTYPE_SYSTEM)))
            .andReturn(results);

        replay(mockTransformer);
        assertTrue(svg.file(SVG_DOCTYPE_PUBLIC, SVG_DOCTYPE_SYSTEM));
        verify(mockTransformer);
    }
}

  

   