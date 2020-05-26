import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.Keys;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import org.openqa.selenium.remote.DesiredCapabilities;
import java.awt.AWTException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class web {
    public static ChromeDriver driver;
    public static String chrome_path = "./chromedriver";
    public static String link = "https://web.whatsapp.com/";
    public static String extension_path = "./InTouchAppPhoneContactsDataSaver.crx";
    public static String csv_path = "./test.csv";
    //public static Actions a;
    public static int count =0;

    public static void initialization() throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", chrome_path);
        // WebDriver driver = new ChromeDriver();
        ChromeOptions options = new ChromeOptions();
        options.addExtensions(new File(extension_path));
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        driver = new ChromeDriver(capabilities);
        //a = new Actions(driver);
        driver.get(link);

        /* step 1: loading the main page and scanning QR code */
        /* method 1 - NEED TO FIX USING TRY CATCH */

        // int timeout = 0;
        // WebElement QRcode1 = driver.findElement(By.className("zCzor"));
        // while(QRcode1.isDisplayed() && timeout < 5){
        // timeout++;
        // Thread.sleep(5000);
        // System.out.println(timeout);
        // }
        // if(timeout == 5){
        // System.exit(0);
        // }
        // Thread.sleep(5000);

        /* method 2 */
        Scanner myObj = new Scanner(System.in);
        System.out.println("STEP 1: Scan Whatsapp QR code \n STEP 2: Click 'Later' on Popup \n STEP 3: Click Red Extension \n STEP 4: Scan InTouchApp QR code & remove pop up by clicking else where\n STEP 5: Press Enter on Terminal");
        myObj.nextLine();
        Thread.sleep(2000);
        myObj.close();
        /*
         * step 2: Click later button on pop up & scan inTouch QR to log in & check for
         * Log in
         */
        // WebElement laterButt = driver.findElement(By.xpath("/html/body/div[3]/div[2]/div[2]/div[2]"));
        // if (laterButt.isDisplayed()) {
        //     laterButt.click();
        //     Thread.sleep(1000);
        //     System.out.println("clicked");
        // } else {
        //     System.out.println("error - cannot find later button");
        //     System.exit(0);
        // }

    }

    public static void automate() throws InterruptedException, IOException, AWTException {
        String[] header;
        String row;
        BufferedReader csvReader = new BufferedReader(new FileReader(csv_path));
        header = csvReader.readLine().split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        while ((row = csvReader.readLine()) != null) {
            String[] data = row.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
            String name = data[3];
            String phone_number = data[20];
            System.out.println(name);
            System.out.println(phone_number);
            String final_message = templating(data, header);
            System.out.println(final_message); //UN-COMMENT IF YOU WANT TO TEST MESSGAE
            // BEFORE SENDING

            //sendMessage(name, phone_number, final_message);
            count++;
        }
        csvReader.close();
    }

    public static void sendMessage(String name, String phone_number, String message)
            throws InterruptedException, AWTException {

        WebElement addContact = driver.findElement(By.xpath("/html/body/div[1]/div[1]/div/div[3]/div/header/div[2]/div/span/div[2]/div/span[1]/img"));
        addContact.click();
        Thread.sleep(1000);
        
        WebElement addNewContact = driver.findElement(By.xpath("/html/body/div[1]/div[1]/div/div[3]/div/header/div[2]/div/span/div[2]/div/span[2]/div/ul/li[1]"));
        addNewContact.click();
        Thread.sleep(1000);
        
        WebElement insertName = driver.findElement(By.xpath("//*[@id='addContactExternalForm']/div/div[1]/input"));
        insertName.sendKeys(name);
        Thread.sleep(1000);

        WebElement insertNumber = driver.findElement(By.xpath("//*[@id='addContactExternalForm']/div/div[4]/input"));
        insertNumber.sendKeys(phone_number);
        Thread.sleep(1000);
        
        WebElement saveChat = driver.findElement(By.xpath("//*[@id='addIntouchContact']"));
        saveChat.click();
        Thread.sleep(5000);

        WebElement insertText = driver.findElement(By.xpath("/html/body/div[1]/div[1]/div/div[4]/div[3]/footer/div[1]/div[2]/div/div[2]"));
        String[] message_split = message.split("\n");
        for(String i : message_split ){
            insertText.sendKeys(i);
            insertText.sendKeys(Keys.chord(Keys.SHIFT, Keys.ENTER));
        }
        Thread.sleep(2000);
        
        //UNCOMMENT BOTTOM 2 LINES OF CODE IF YOU WANT TO CHECK EACH MESSAGE
        //Scanner myObj2 = new Scanner(System.in);
        //myObj2.nextLine();
        //UNCOMMENT TOP 2 LINES OF CODE IF YOU WANT TO CHECK EACH MESSAGE

        WebElement sendChat = driver.findElement(By.xpath("/html/body/div[1]/div[1]/div/div[4]/div[3]/footer/div[1]/div[3]/button"));
        sendChat.click();
        Thread.sleep(5000);
        

    }

    public static String templating(String[] data, String[] header) {
        String name, qty, item_name, price,address, delivery_cost, delivery_date, message,recipient_name;
        //ArrayList<String> order_line = new ArrayList<String>();
        double total_cost = 0;
        String orderline = "";
        recipient_name = data[24];
        address = data[21]+ " " + data[22]; 
        System.out.println(address);
        delivery_date = data[19].substring(1,data[19].length()-1);
        name = data[3];
        delivery_cost = data[17];
        message=data[27];
        for (int i = 0; i < 10; i++){
            int index = i + 4;
            if(data[index].isEmpty()){
                continue;
            }
            else{
                qty = data[index];
                price = header[index].substring(1,3);
                item_name = header[index].substring(8);
                orderline += (qty + " * " + item_name + " $" + price + "\n");
                total_cost += Double.parseDouble(qty) * Double.parseDouble(price);
                //order_line.add(qty + " * " + item_name + " " + price);
            }    
        }
        total_cost += Double.parseDouble(delivery_cost);
        // String temp = String.format("THIS IS A TESTER MESSAGE PLEASE IGNORE \n Thank you for ordering with TiapTiapWithSoph, here is your order summary 🥳 - \n\nName:\n%s \n\n📆 Time of Order:\n%s \n\n📝 Order:\n%s\n\n🏡 Delivery Address:\n%s\n\n🚚 Delivery cost & date:\n$%s,%s\n\n💵 Total cost:\n%.2f\nPlease make your payment to 90089066 via paylah or paynow. Once you have made the payment, please send a screenshot to the number together with this order form to complete the order process 🎉\n",name,time_stamp,orderline,address,delivery_cost,delivery_date,total_cost);
        String temp = String.format("Thank you for ordering with TiapTiapWithSoph <3, here is your order summary - \n\nSender's Name:\n%s \n\nRecipient's Name:\n%s \n\nOrder:\n%s\n\nDelivery Address:\n%s\n\nDelivery cost & date:\n$%s,%s\n\nTotal cost:\n%.2f\n\nMessage:\n%s\n\nPlease make your payment to 90089066 via paylah or paynow. You can also bank transfer to POSB 051160410. Once you have made the payment, please send a screenshot to the number together with this order form to complete the order process <3 \n",name,recipient_name,orderline,address,delivery_cost,delivery_date,total_cost,message);
        //String template = "Name:\n{name}\n📆 Time of Order:\n{time_stamp}\n📝 Order:\n{qty} * (item_name) $(price)\n🏡 Delivery Address:\n{address}\n🚚 Delivery cost:\n{delivery_cost}\n💵 Total cost:\n{total_cost}\nPlease make your payment to 90089066 via paylah or paynow. Once you have made the payment, please send a screenshot to the number together with this order form to complete the order process 🎉";
        return temp;
    }

    
    public static void main(String[] args) throws InterruptedException, IOException, AWTException {
        // initialization();
        automate();
        System.out.println("SUCCESS");

    }    
}