public boolean sendKeys(By element, Keys text) {
        try {
            driver.findElement(element).sendKeys(text);
        } catch (Exception e) {
            logger.error("Element Not clicked " + e);
        }
        return true;
    }

    public boolean sendKeys(By element, String text) {
        try {
            driver.findElement(element).clear();
            toHighlight(element);
            logger.info(text +" entered in element: " + element);
            driver.findElement(element).sendKeys(text);
        } catch (Exception e) {
            logger.error("Element Not clicked " + e);
        }
        return true;
    }

    public boolean clearText(By element) {
        try {
            WebElement el = driver.findElement(element);
            if (el.getTagName().equalsIgnoreCase("input") && el.getAttribute("type").equalsIgnoreCase("text")) {
                driver.findElement(element).clear();
            }

        } catch (Exception e) {
            logger.error(e);
        }
        return true;
    }

    protected boolean waitForStatus(By ele, String taSTatus) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, 20);
            return wait.until(ExpectedConditions.textToBePresentInElementLocated(ele, taSTatus));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void actionClick(By element) {
        WebElement webElement = driver.findElement(element);
        Actions action =new Actions(driver);
        action.moveToElement(webElement).click().perform();
    }

    public void actionClick(WebElement element) {
        Actions action =new Actions(driver);
        action.moveToElement(element).click().build().perform();
    }

    public boolean click(By element) throws Exception {
        try {
            logger.info("Clicking object using By element : " + element);
            try {
                waitUntilElementIsClickable(element, expWait);
            } catch (Exception e) {
                e.printStackTrace();
            }
            logger.info("Element is clickable with condition Implict wait");
            WebElement webElement = driver.findElement(element);
            if (webElement.isEnabled() && webElement.isDisplayed()) {
                logger.info("Element is enabled or displayed in page");
                toHighlight(element);
                webElement.click();
                return true;
            } else {
                logger.error("Element is not enabled or displayed for click, will try javascript click next.");
                return clickElementUsingJavaScript(element);
            }
        } catch (ElementNotInteractableException e) {
            logger.error("Element not interactable during click " + e.getMessage());

            return clickElementUsingJavaScript(element);
        } catch (WebDriverException e) {
            e.printStackTrace();
            logger.error("WebDriver exception during click " + e.getMessage());
            if (e.getMessage().contains("Missing Template ERR_CONNECT_FAIL")) {
                new WebDriverWait(driver, expWait)
                        .until(ExpectedConditions.elementToBeClickable(element)).click();
                return true;
            } else
                return clickElementUsingJavaScript(element);
        } catch (Exception e) {
            e.printStackTrace();
            return clickElementUsingJavaScript(element);
        }
    }

    public boolean moveToElement(By element) throws Exception {
        try {
            logger.info("Moving to element : " + element);
            try {
                waitUntilElementIsClickable(element, expWait);
            } catch (Exception e) {
                e.printStackTrace();
            }
            logger.info("Element is clickable with condition Implict wait");
            WebElement webElement = driver.findElement(element);
            if (webElement.isEnabled() && webElement.isDisplayed()) {
                logger.info("Element is enabled or displayed in page");
                toHighlight(element);
                Actions action =new Actions(driver);
                action.moveToElement(webElement).perform();

                return true;
            }
        } catch (ElementNotInteractableException e) {
            logger.error("Element not interactable during move " + e.getMessage());
        }
        return false;
    }

    public boolean click(WebElement webElement) {
        try {
            logger.info("Clicking object using By element : " + webElement);
            try {
                waitUntilElementIsClickable(webElement, expWait);
            } catch (Exception e) {

            }
            logger.info("Element is clickable with condition Implict wait");

            if (webElement.isEnabled() && webElement.isDisplayed()) {
                logger.info("Element is enabled or displayed in page");
                webElement.click();
                return true;
            }
        } catch (WebDriverException e) {
            e.printStackTrace();
            logger.error("WebDriver exception during click " + e.getMessage());
            if (e.getMessage().contains("Missing Template ERR_CONNECT_FAIL")) {
                new WebDriverWait(driver, expWait)
                        .until(ExpectedConditions.elementToBeClickable(webElement)).click();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public boolean waitUntilElementIsClickable(By locator, long seconds) {
        WebElement element = null;
        try {
            new WebDriverWait(driver, seconds).until(ExpectedConditions.elementToBeClickable(locator));
        } catch (Exception e) {
            logger.info("Failed to wait for element to be clickable");
            throw e;
        }
        return true;
    }

    public boolean waitUntilElementIsInvisible(By locator, long seconds) {
        WebElement element = null;
        try {
            new WebDriverWait(driver, seconds).until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (Exception e) {
            logger.info("Failed to wait for element to be invisible");
            throw e;
        }
        return true;
    }

    public boolean waitUntilElementIsClickable(WebElement element, long seconds) throws Exception {
        try {
            new WebDriverWait(driver, seconds).until(ExpectedConditions.elementToBeClickable(element));
        } catch (Exception e) {
            logger.info("Failed to wait for element to be clickable");
            throw e;
        }
        return true;
    }

    public boolean clickElementUsingJavaScript(By locator) throws Exception {
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        try {
            try {
                waitUntilElementIsClickable(locator, expWait);
            } catch (Exception e) {
                // do nothing, continue to try and click element
            }
            jse.executeScript("arguments[0].click();", driver.findElement(locator));

            return true;
        } catch (TimeoutException e) {
            throw new Exception("Element " + locator.toString() + " was not found\n" + e.getMessage(), e);
        } catch (WebDriverException e) {
            if (e.getMessage().contains("JavaScript error")) {
                logger.warn("Skipping exception with JavaScript error");
            } else if (!e.getMessage().contains("Missing Template ERR_CONNECT_FAIL")) {
                logger.info("Failed to click: " + locator + " by javascript. Retrying..");
                jse.executeScript("arguments[0].click();",
                        new WebDriverWait(driver, expWait)
                                .until(ExpectedConditions.elementToBeClickable(locator)));
                return true;
            } else
                throw new Exception("Web driver exception clicking element with javascript " + locator.toString() + "\n"
                        + e.getMessage());
        } catch (Exception e) {
        }
        return false;
    }

    public boolean clickElementUsingJavaScript(WebElement locator) throws Exception {
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        try {
            try {
                waitUntilElementIsClickable(locator, expWait);
            } catch (Exception e) {
                // do nothing, continue to try and click element
            }
            jse.executeScript("arguments[0].click();", locator);

            return true;
        } catch (TimeoutException e) {
            throw new Exception("Element " + locator.toString() + " was not found\n" + e.getMessage(), e);
        } catch (WebDriverException e) {
            if (e.getMessage().contains("JavaScript error")) {
                logger.warn("Skipping exception with JavaScript error");
            } else if (!e.getMessage().contains("Missing Template ERR_CONNECT_FAIL")) {
                logger.info("Failed to click: " + locator + " by javascript. Retrying..");
                jse.executeScript("arguments[0].click();",
                        new WebDriverWait(driver, expWait)
                                .until(ExpectedConditions.elementToBeClickable(locator)));
                return true;
            } else
                throw new Exception("Web driver exception clicking element with javascript " + locator.toString() + "\n"
                        + e.getMessage());
        } catch (Exception e) {
        }
        return false;
    }

    public boolean isElementCurrentlyDisplayed(By element) throws Exception {
        boolean isDisplayed = false;
        List<WebElement> elementList = driver.findElements(element);
        if (elementList.size() <= 0) {
            return false;
        } else if (elementList.size() > 1) {
            throw new Exception("Error: Found multiple elements");
        } else {
            WebElement foundElement = elementList.get(0);
            if (foundElement.isDisplayed()) {
                isDisplayed = true;
            }
            return isDisplayed;
        }
    }

    public String getTextFromElement(By element) throws Exception {
        try {
            logger.info("Getting text from element : " + element + "");
            String innerText = new WebDriverWait(driver, expWait)
                    .until(ExpectedConditions.visibilityOfElementLocated(element)).getText().trim();
            logger.info("The Inner Text Of An Element is : " + innerText);
            return innerText;
        } catch (StaleElementReferenceException e) {
            return new WebDriverWait(driver, expWait)
                    .until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(element)))
                    .getText();
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.getMessage());
            if (e.getMessage().contains("Missing Template ERR_CONNECT_FAIL"))
                return new WebDriverWait(driver, expWait)
                        .until(ExpectedConditions.visibilityOfElementLocated(element)).getText();
            else
                throw new Exception(e);
        }
    }

    public String getTextFromElements(WebElement element) throws Exception {
        try {
            logger.info("Getting text from element : " + element + "");
            String innerText = new WebDriverWait(driver, expWait)
                    .until(ExpectedConditions.visibilityOf(element)).getText().trim();
            logger.info("The Inner Text Of An Element is : " + innerText);
            return innerText;
        } catch (StaleElementReferenceException e) {
            return new WebDriverWait(driver, expWait)
                    .until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOf(element)))
                    .getText();
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.getMessage());
            if (e.getMessage().contains("Missing Template ERR_CONNECT_FAIL"))
                return new WebDriverWait(driver, expWait)
                        .until(ExpectedConditions.visibilityOf(element)).getText();
            else
                throw new Exception(e);
        }
    }

    public String getAttributeValueFromElement(By element, String attribute) throws Exception {
        try {
            logger.info("Getting text from element : " + element + "");
            String innerText = new WebDriverWait(driver, expWait)
                    .until(ExpectedConditions.visibilityOfElementLocated(element)).getAttribute(attribute).trim();
            logger.info("The attribute value of an Element is : " + innerText);
            return innerText;
        } catch (StaleElementReferenceException e) {
            return new WebDriverWait(driver, expWait)
                    .until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(element)))
                    .getAttribute(attribute);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.getMessage());
            if (e.getMessage().contains("Missing Template ERR_CONNECT_FAIL"))
                return new WebDriverWait(driver, expWait)
                        .until(ExpectedConditions.visibilityOfElementLocated(element)).getAttribute(attribute);
            else
                throw new Exception(e);
        }
    }

    public String getAttributeValueFromElement(WebElement element, String attribute) throws Exception {
        try {
            logger.info("Getting text from element : " + element + "");
            String innerText = new WebDriverWait(driver, expWait)
                    .until(ExpectedConditions.visibilityOf(element)).getAttribute(attribute).trim();
            logger.info("The attribute value of an Element is : " + innerText);
            return innerText;
        } catch (StaleElementReferenceException e) {
            return new WebDriverWait(driver, expWait)
                    .until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOf(element)))
                    .getAttribute(attribute);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.getMessage());
            if (e.getMessage().contains("Missing Template ERR_CONNECT_FAIL"))
                return new WebDriverWait(driver, expWait)
                        .until(ExpectedConditions.visibilityOf(element)).getAttribute(attribute);
            else
                throw new Exception(e);
        }
    }

    public String getAttributeValueFromElements(String attribute, WebElement element) throws Exception {
        try {
            logger.info("Getting text from element : " + element + "");
            String innerText = new WebDriverWait(driver, expWait)
                    .until(ExpectedConditions.visibilityOf(element)).getAttribute(attribute).trim();
            logger.info("The attribute value of an Element is : " + innerText);
            return innerText;
        } catch (StaleElementReferenceException e) {
            return new WebDriverWait(driver, expWait)
                    .until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOf(element)))
                    .getAttribute(attribute);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.getMessage());
            if (e.getMessage().contains("Missing Template ERR_CONNECT_FAIL"))
                return new WebDriverWait(driver, expWait)
                        .until(ExpectedConditions.visibilityOf(element)).getAttribute(attribute);
            else
                throw new Exception(e);
        }
    }

    public boolean isElementEnabled(By element) {
        WebElement ele = driver.findElement(element);
        if (ele.isEnabled()) {
            logger.info("Element enabled");
            return true;
        } else
            return false;
    }

    public boolean isElementSelected(By element) throws Exception {
        WebElement ele = driver.findElement(element);
        if (ele.isSelected()) {
            logger.info("Element enabled");
            return true;
        } else
            return false;
    }

    public boolean waitForElementToDisplay(By locator, long maxSecondsToWait) {
        for (int i = 0; i < maxSecondsToWait; i++) {
            try {
                Thread.sleep(1000);
                if (isElementCurrentlyDisplayed(locator)) {
                    return true;
                }
            } catch (Exception e) {
                // do nothing, let it keep looping to wait for object
            }
        }
        return false;
    }

    public boolean waitForElementToDisplay(WebElement locator, long maxSecondsToWait) {
        for (int i = 0; i < maxSecondsToWait; i++) {
            try {
                Thread.sleep(1000);
                if (locator.isDisplayed()) {
                    return true;
                }
            } catch (Exception e) {
                // do nothing, let it keep looping to wait for object
            }
        }
        return false;
    }

    public void pause(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ie) {
        }
    }

    private void toHighlight(By element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].setAttribute('style','background:yellow;border: 2px solid red;');", driver.findElement(element));
    }

    private void toRemoveHighlight(By element){
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].setAttribute('style','background:nill;border: nill;');", driver.findElement(element));
        }catch(StaleElementReferenceException e) {
            e.printStackTrace();
        }
    }
