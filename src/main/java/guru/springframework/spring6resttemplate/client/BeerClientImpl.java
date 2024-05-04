package guru.springframework.spring6resttemplate.client;

import com.fasterxml.jackson.databind.JsonNode;
import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerDTOPageImpl;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BeerClientImpl implements BeerClient {

    private final RestTemplateBuilder restTemplateBuilder;

    private static final String GET_BEER_PATH = "/api/v1/beer";
    private static final String GET_BEER_PATH_BY_ID = "/api/v1/beer/{beerId}";

    @Override
    public Page<BeerDTO> listBeers(String beerName, BeerStyle bearStyle, Boolean showInventory, Integer pageNumber, Integer pageSize) {
        RestTemplate restTemplate = restTemplateBuilder.build();

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(GET_BEER_PATH);

        if(beerName != null) {
            builder.queryParam("beerName", beerName);
        }

        if(bearStyle != null) {
            builder.queryParam("bearStyle", bearStyle);
        }

        if (showInventory != null) {
            builder.queryParam("showInventory", showInventory);
        }

        if (pageNumber != null) {
            builder.queryParam("pageNumber", pageNumber);
        }

        if (pageSize != null) {
            builder.queryParam("pageSize", pageSize);
        }



        ResponseEntity<BeerDTOPageImpl> pageResponse = restTemplate.getForEntity(builder.toUriString(), BeerDTOPageImpl.class);

        return pageResponse.getBody();
    }

    @Override
    public Page<BeerDTO> listBeers() {
        return listBeers(null, null, null, null, null);
    }

    @Override
    public BeerDTO getBeerById(UUID beerId) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        return restTemplate.getForObject(GET_BEER_PATH_BY_ID, BeerDTO.class, beerId);

    }

    @Override
    public BeerDTO createBeer(BeerDTO newDto) {
        RestTemplate restTemplate = restTemplateBuilder.build();

        URI uri = restTemplate.postForLocation(GET_BEER_PATH, newDto);
        return restTemplate.getForObject(uri.getPath(), BeerDTO.class);
    }

    @Override
    public BeerDTO updateBeer(BeerDTO beerDto) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        restTemplate.put(GET_BEER_PATH_BY_ID, beerDto, beerDto.getId());
        return getBeerById(beerDto.getId());
    }

    @Override
    public void deleteBeer(UUID beerId) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        restTemplate.delete(GET_BEER_PATH_BY_ID, beerId);
    }
}
