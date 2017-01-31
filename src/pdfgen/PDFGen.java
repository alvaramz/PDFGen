/*
 * Ejemplo de uso de la biblioteca Apache PDFBox para generar un PDF.
 * Este ejemplo es una reformularión del que se encuntra en el cookbook de la versión 1.8: https://pdfbox.apache.org/1.8/cookbook/documentcreation.html
 * 
 * Elaborado por el Ing. Adrián Alvarado Rmaírez, enero del 2017.
 * 
 */
package pdfgen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.encoding.Encoding;
import org.apache.pdfbox.util.Matrix;

/**
 *
 * @author Adrián Alvarado Ramírez.
 */
public class PDFGen {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        PDDocument documento = new PDDocument();
        PDPage pagina = new PDPage(PDRectangle.LETTER);
        documento.addPage(pagina);
        System.out.println("Tamaño de la página: Ancho " + pagina.getArtBox().getWidth() + ", Alto " + pagina.getArtBox().getHeight());

        PDFont fuenteTexto = null;
        PDFont fuentePie = null;

        // Establece la fuente del texto
        try {
            fuenteTexto = PDType0Font.load(documento, new File("Nunito-Bold.ttf"));
        } catch (IOException ioe) {
            fuenteTexto = PDType1Font.TIMES_ROMAN;
        }


        // Establece la fuente del pie de página
        try {
            fuentePie = PDType0Font.load(documento, new File("SourceSansPro-Regular.ttf"));
        } catch (IOException ioe) {
            fuentePie = PDType1Font.TIMES_ROMAN;
        }



        try {
            PDPageContentStream contenido = new PDPageContentStream(documento, pagina);
            contenido.beginText();

            contenido.setFont(fuentePie, 10);
            contenido.newLineAtOffset(300, 20); // Se crea una  línea para escribir el pie de página.
            contenido.showText("Pie de página");
            contenido.newLineAtOffset(-300, 700); // Se pone al principio de la página.
            contenido.setFont(fuenteTexto, 12);
            //  contenido.showText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam euismod, risus sit amet fermentum varius, felis est pretium odio, non euismod nisi massa at felis. Interdum et malesuada fames ac ante ipsum primis in faucibus. Duis molestie neque ac sapien fermentum dignissim. Quisque vitae laoreet lorem. Cras est massa, sagittis in dui ut, venenatis pretium lectus. Nam interdum erat sed tempus aliquam. Etiam sit amet mi a ligula euismod tristique. Praesent quis vulputate justo, ut dignissim ex. Vestibulum non laoreet orci. Etiam iaculis dolor ut velit faucibus bibendum. Nulla facilisi. Sed eget erat bibendum, scelerisque odio nec, accumsan elit. Ut vulputate vehicula lorem at mattis. Pellentesque justo orci, dapibus in ullamcorper interdum, mollis vel metus. In est lorem, pellentesque aliquet felis non, faucibus pellentesque enim. Duis hendrerit a nulla quis eleifend. ");
            String loremIpsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam euismod, risus sit amet fermentum varius, felis est pretium odio, non euismod nisi massa at felis. Interdum et malesuada fames ac ante ipsum primis in faucibus. Duis molestie neque ac sapien fermentum dignissim. Quisque vitae laoreet lorem. Cras est massa, sagittis in dui ut, venenatis pretium lectus. Nam interdum erat sed tempus aliquam. Etiam sit amet mi a ligula euismod tristique. Praesent quis vulputate justo, ut dignissim ex. Vestibulum non laoreet orci. Etiam iaculis dolor ut velit faucibus bibendum. Nulla facilisi. Sed eget erat bibendum, scelerisque odio nec, accumsan elit. Ut vulputate vehicula lorem at mattis. Pellentesque justo orci, dapibus in ullamcorper interdum, mollis vel metus. In est lorem, pellentesque aliquet felis non, faucibus pellentesque enim. Duis hendrerit a nulla quis eleifend. ";
            escribirTextoEnLineas(loremIpsum, pagina, contenido, fuenteTexto, 12);

            contenido.endText();

            contenido.close();

            documento.save("Prueba.pdf");
            documento.close();
        } catch (IOException ioe) {
            System.out.print(ioe);
        }

    }

    public static String escribirTextoEnLineas(String texto, PDPage pagina, PDPageContentStream contenido, PDFont fuente, float tamanoFuente) throws IOException {
        float anchoPagina = pagina.getArtBox().getWidth();
        float margen = 30; // Para cada lado, el margen es de 30.
        float anchoLetra = fuente.getAverageFontWidth() / 1000 * tamanoFuente;
        
        int maximoLetrasPorLinea = (int)Math.ceil((anchoPagina - 2 * margen)/anchoLetra);
        
        // Se crean las línea, cada línea puede tener como máximo el valor de maximoLetrasPorLinea
        ArrayList <String> lineas = new ArrayList<String>();
       
        int indiceInicioSubstr = 0;
        int indiceUltimoSubstr = 0;
        int indiceUltimoEspacio = 0;
        while(indiceUltimoSubstr < texto.length()){
            indiceInicioSubstr = indiceUltimoSubstr;
            
            if(indiceInicioSubstr + maximoLetrasPorLinea >= texto.length()){             
                indiceUltimoSubstr = texto.length();
            }else{                
                indiceUltimoSubstr += maximoLetrasPorLinea;
            }
            
            String linea = texto.substring(indiceInicioSubstr, indiceUltimoSubstr);
            // Rectifica para solo cortar por espacios
            indiceUltimoEspacio = linea.lastIndexOf(" ");
            if(indiceUltimoEspacio > 0 && indiceUltimoEspacio < indiceUltimoSubstr){
                indiceUltimoSubstr = indiceInicioSubstr + indiceUltimoEspacio + 1;
                linea = texto.substring(indiceInicioSubstr, indiceUltimoSubstr);
            }
            
            lineas.add(linea);
        }
        
        contenido.newLineAtOffset(margen,0);
        // Se imprime cada línea
        for(String linea : lineas){
            float anchoLinea = fuente.getStringWidth(linea) / 1000 * tamanoFuente;
            contenido.showText(linea);
            contenido.newLineAtOffset(0, -(tamanoFuente + 10)); 
        }
        


        System.out.print(maximoLetrasPorLinea);


        return null;
    }
}
