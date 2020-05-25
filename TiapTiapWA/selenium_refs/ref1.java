package selenium_refs;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;


class ref1 {
    public static void main(String[] args) {
        System.setProperty("webdriver.gecko.driver", "./geckodriver");
        WebDriver driver = new FirefoxDriver();
        String link = "https://istd.sutd.edu.sg/";
        driver.get(link);        

        java.util.List<WebElement> links = driver.findElements(By.tagName("a"));
        
        for (int i=0; i<links.size();i++)
        {
            WebElement webElement = links.get(i);
            String next = webElement.getAttribute("href");
            if (next == null || next == link) {
                continue;
            }
            
            boolean stale = true;
            while (stale) {
                try {                    
                    driver.navigate().to(next);
                    Thread.sleep(3000);                    

                    driver.navigate().back();
                    // driver.navigate().to(link);
                    links = driver.findElements(By.tagName("a"));
                    stale = false;
                } catch (StaleElementReferenceException e) {
                    stale = true;
                } catch(Exception e){
                    stale = true;
                }
            }
        }
    }    
}