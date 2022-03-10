package ru.kamuzta.rollfactorymgr.service.webservice;

import com.google.inject.ImplementedBy;
import ru.kamuzta.rollfactorymgr.exception.WebServiceException;
import ru.kamuzta.rollfactorymgr.model.roll.*;

import java.math.BigDecimal;
import java.util.List;

@ImplementedBy(RollServiceMock.class)
public interface RollService {
    /**
     * Update local cached Registry by values from server
     *
     * @throws WebServiceException
     */
    void updateRegistryFromServer() throws WebServiceException;

    /**
     * Get local cached Roll Registry
     *
     * @return local cached Roll Registry
     * @throws WebServiceException
     */
    List<Roll> getLocalRollRegistry();

    /**
     * Find Roll by SKU in local cached Roll Registry
     *
     * @param sku SKU of Roll
     * @return found Roll
     * @throws WebServiceException
     */
    Roll findRollBySku(String sku) throws WebServiceException;

    /**
     * Find Roll by SKU pattern in local cached Roll Registry
     *
     * @param sku SKU/part of SKU of Roll
     * @return list of matched Rolls
     * @throws WebServiceException
     */
    List<Roll> findRollBySkuPattern(String sku) throws WebServiceException;

    /**
     * Find Roll by parameters in local cached Roll Registry
     *
     * @param id  part of roll id
     * @param sku  part of roll sku
     * @param rollType  roll manufacturing method
     * @param paper     raw material
     * @param widthType roll width
     * @param coreType  roll core
     * @param value     main parameter(length or diameter)
     * @return list of matched Rolls
     * @throws WebServiceException
     */
    List<Roll> findRollByParams(Long id, String sku, RollType rollType, Paper paper, WidthType widthType, CoreType coreType, BigDecimal value) throws WebServiceException;

    /**
     * Create Roll on Server Registry
     *
     * @param sku       SKU of new Roll
     * @param rollType  roll manufacturing method
     * @param paper     raw material
     * @param widthType roll width
     * @param coreType  roll core
     * @param value     main parameter(length or diameter)
     * @return new Roll
     * @throws WebServiceException if validation fail
     */
    Roll createRoll(String sku, RollType rollType, Paper paper, WidthType widthType, CoreType coreType, BigDecimal value) throws WebServiceException;

    /**
     * Remove Roll on Server Registry by SKU
     * @param sku SKU of Roll to remove
     * @return true if success
     * @throws WebServiceException if Roll with specified SKU was not found or there is some orders with this roll
     */
    boolean removeRollBySku(String sku) throws WebServiceException;

    /**
     * Update Roll on Server Registry with new parameters
     * @param roll - Roll with same SKU but diffirent parameters
     * @return updated Roll
     * @throws WebServiceException if validation fail or all parameters are equals
     */
    Roll updateRoll(Roll roll) throws WebServiceException;

}
