package max.coreSources;



import max.util.IHandlerInput;

import javax.xml.bind.JAXBException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;

public class Factory implements Serializable {
    private static final long serialVersionUID = -8514622879534406859L;
    private IHandlerInput inputHandler;


    /**
     *
     * Read what the inputs from the file and create the instance if
     * everything is successfully validated and formatted
     *
     * @param inputs fields from the file to fill the Dragon attrs
     * @return instance of a Dragon with the input entered or null if error
     */
    public Organization generateFromScript(ArrayList<String> inputs) {
        try {
            Integer userID = (Integer) getValueOf(Integer.class, inputs.get(0));
            String name = (String) getValueOf(String.class, inputs.get(1));

            Long x = (Long) getValueOf(Long.class, inputs.get(2));
            Float y = (Float) getValueOf(Float.class, inputs.get(3));
            Coordinates coordinates = new Coordinates(x,y);

            Long annualTurnover = (Long) getValueOf(Long.class, inputs.get(4));
            Color oColor = (Color) getValueOf(Color.class, inputs.get(5));
            String fullName = (String) getValueOf(String.class, inputs.get(6));
            OrganizationType oType = (OrganizationType) getValueOf(OrganizationType.class, inputs.get(7));
            Address officialAddress = (Address) getValueOf(Address.class, inputs.get(8));



            if (x > -328 && annualTurnover > 0)
                return new Organization(userID, name, coordinates, annualTurnover, oColor, fullName, oType, officialAddress);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | IndexOutOfBoundsException | NullPointerException ex) {
            return null;
        }
        return null;
    }
    /**
     *
     * Read what the user writes on, validates it and create the instance if
     * everything is successfull
     *
     * @param inputHandler manages all related with the IO
     * @return instance of a Dragon with the input entered
     */
    public Organization generateOrganizationByInput(IHandlerInput inputHandler)
    {
        this.inputHandler = inputHandler;

        String name = (String) validateOrganizationProp("name", "", false, String.class);

        Long x = (Long) this.validateOrganizationProp("coordinate{X}", " [should be more than -328]", false, Long.class, -328);
        Float y = (Float) this.validateOrganizationProp("coordinate{Y}", "", false, Float.class);
        Coordinates coordinates = new Coordinates(x,y);
        Color oColor = (Color) validateOrganizationProp("color", Arrays.asList(Color.values()).toString(), false, Color.class);
        String fullName = (String) validateOrganizationProp("name", "", false, String.class);
        Long annualTurnover= (Long) validateOrganizationProp("annualTurnover"," [should be more than 0 and not null]", false, Long.class, 0);
        Address officialAddress = (Address) validateOrganizationProp("address", "[should be not null]", false, Address.class);
        OrganizationType oType = (OrganizationType) validateOrganizationProp("organizationType", Arrays.asList(OrganizationType.values()).toString(), false, OrganizationType.class);




        return new Organization(name, coordinates, oColor, fullName, annualTurnover, officialAddress, oType);
    }

    /**
     *
     * Reads the clients input and validates if is nullable or the input is in a incorrect
     * format till it is successfully
     *
     * @param fType type of the input, just a String for printing
     * @param desc description of the validation rules, just for printing
     * @param nullable boolean if the value could be nullable
     * @param toClass class of the Object that we want to validate
     * @return Object that represents the validated wanted value
     */
    private Object validateOrganizationProp(String fType, String desc, boolean nullable, Class<?> toClass) {
        Object obj = null;
        String input = "";
        boolean errorHappened;
        do {
            errorHappened = false;
            input = inputHandler.readWithMessage("Organization's " +fType+desc+": ");

            if (nullable && input.isEmpty())
                return null;

            try {
                obj = getValueOf(toClass, input);
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                inputHandler.printLn(1, "Invalid value passed!");
                errorHappened = true;
            }
            input = input.isEmpty() ? null : input;
        } while ((!nullable && input == null) || errorHappened);
        return obj;
    }

    /**
     *
     * Validates if the input comply with the validation rules till it is correct.
     *
     * @param fType type of the input, just a String for printing
     * @param desc description of the validation rules, just for printing
     * @param nullable boolean if the value could be nullable
     * @param toClass class of the Object that we want to validate
     * @param min validation rule for numbers
     * @return Object that represents the validated wanted value
     */
    private Object validateOrganizationProp(String fType, String desc, boolean nullable, Class<?> toClass, int min) {
        Object obj = null;
        do {
            obj = validateOrganizationProp(fType, desc, nullable, toClass);
            if (obj == null)
                return null;
        } while (!checkValidNumber((toClass.equals(Long.class)) ? (Long) obj : (Double) obj, min));
        return obj;
    }

    /**
     *
     * check if the number comply the validation rule
     *
     * @param num Long representing the number to validate
     * @param min int validation rule
     * @return is bigger than the validation rule
     */
    private boolean checkValidNumber(Long num, int min) {
        return num > min;
    }

    /**
     *
     * check if the number comply the validation rule
     *
     * @param num Double representing the number to validate
     * @param min int validation rule
     * @return is bigger than the validation rule
     */
    private boolean checkValidNumber(Double num, int min) {
        return num > min;
    }

    /**
     *
     * gets the Object resulted from the valueOf method inside the passed class
     *
     * @param toClass class from which we want to get the method
     * @param s parameter of the method
     * @return the Object got after running the valueOf()
     * @throws InvocationTargetException happens when the invoke method has a error
     * @throws IllegalAccessException when the method invoked has no access to the specified class
     * @throws NoSuchMethodException when the method is not found in the passed class
     */
    private static Object getValueOf(Class<?> toClass, String s) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        if (toClass.equals(String.class) || s.equals("")) return s;
        Method method = toClass.getMethod("valueOf", String.class);
        return method.invoke(null, s);
    }

}