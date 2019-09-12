---
title:  JCV pour tester simplement et efficacement ses APIs REST en JAVA
author: Léo Millon
---

* Tester ses APIs REST en Java
  * Pourquoi tester ses APIs ?
  * Que souhaite-t-on tester ?
    * Pertinence des situations et jeux de données
    * Valider le contenu de la réponse de l’API
  * Comment la mise en place de ces tests pourrait être plus simple et efficace ?
* {#JCV#} pour vous aider
  * Qu’est ce que {#JCV#} ?
  * Exemple
  * Quels sont les avantages apportés par cet outil ?
* De la théorie à la pratique
  * Présentation du cas pratique
  * L’API REST à tester
  * Installation de {#JCV#}
  * Mise en place des tests
    * Générer la réponse de référence
    * Valider le test auprès de la réponse de référence
    * Adapter la réponse de référence avec des validateurs
  * Aller plus loin

------------

## Tester ses APIs REST en Java
Lorsque vous exposez des APIs REST, que ce soit pour votre usage interne ou pour des consommateurs externes, il est important de les tester.

### Pourquoi tester ses APIs ?
Tester les APIs que l’on expose permet de remplir plusieurs objectifs :
* S’assurer du bon fonctionnement du développement.
* Vérifier que la sécurité est bien appliquée sur celles qui le requièrent.
* Suivre les évolutions au fil du temps pour éviter d’appliquer des “breaking changes” sans le vouloir.
* Donner des exemples d’utilisation.

### Que souhaite-t-on tester ?
Pour pouvoir atteindre les objectifs listés précédemment, il y a deux points essentiels à considérer : établir les situations/jeux de données à appliquer puis valider le contenu de chaque réponse retournée par l’API.

#### Pertinence des situations et jeux de données
En effet, lorsque vous exposez une API, celle-ci va être utilisée dans un but défini à l’avance et avec une certaine cohérence des données transmises.

Il ne tiendra qu’à vous d’être capable de lister les différentes situations intéressantes et qui méritent d’être testées avec des jeux de données particuliers.

#### Valider le contenu de la réponse de l’API
Après avoir choisi votre situation et votre jeu de données, vous appelez l’API concernée et recevez une réponse qu’il va falloir contrôler afin de savoir si le résultat obtenu est bien cohérent avec celui attendu.

La réponse est composée de plusieurs parties :
* Le statut HTTP (200 - OK, 404 - Not Found, etc.)
* Les en-têtes (“headers”)
* Le corps (“body”)

Si on se concentre sur le corps de la réponse, il est intéressant de vérifier que le contenu est correct en profondeur. Pour cela il faut vérifier que les champs attendus soient présents, que les valeurs de ces champs soient les bonnes et potentiellement que l’ordonnancement des éléments (dans les tableaux par exemple) soit respecté.

Sans identifier une technologie en particulier, la démarche habituellement rencontrée consiste à extraire les champs de la réponse (via différentes méthodes : JSON dé-sérialisé en objet du langage ou exploration via des requêtes [JSONPath](https://restfulapi.net/json-jsonpath/)) et de les tester indépendamment.

##### Qu’est ce que cela veut dire concrètement ?
Imaginons que la réponse de votre API (disponible sur “http://localhost:8080/lotto/{id}”) retourne une réponse contenant ce corps en JSON :

```json
{
  "lotto": {
    "lottoId": 5,
    "winning-numbers": [2, 45, 34, 23, 7, 5, 3],
    "winners": [
      {
        "winnerId": 23,
        "numbers": [2, 45, 34, 23, 3, 5]
      },
      {
        "winnerId": 54,
        "numbers": [52, 3, 12, 11, 18, 22]
      }
    ]
  }
}
```

Voici le genre de test d’API via une validation des champs par JSONPath (dans un test [JUnit](https://junit.org/junit5/) avec [RESTAssured](http://rest-assured.io/)), que nous pouvons obtenir :

```java
@Test public void
lotto_resource_returns_200_with_expected_id_and_winners() {

    when().
            get("/lotto/{id}", 5).
    then().
            statusCode(200).
            body("lotto.lottoId", equalTo(5),
                 "lotto.winners.winnerId", hasItems(23, 54));

}
```
(exemple issu du site de [RESTAssured](http://rest-assured.io/)).

Nous pouvons constater sur cet exemple que le statut de la réponse est contrôlé ainsi que quelques champs de la réponse. Cependant il faut avoir un minimum de connaissances pour pouvoir écrire certaines requêtes et le résultat présenté ci-dessus ne nous donne pas une vision claire de la réponse retournée, ni de de l'exhaustivité des champs testés (aucune info ne nous permet de savoir s’il y a un 3ème “winner” dans la réponse sans avoir à complexifier le test).

En résumé, cette méthode fonctionne mais a tout de même quelques inconvénients :
* L’extraction des champs peut s’avérer fastidieuse et chronophage, ce qui peut provoquer l’écriture de tests plus ou moins bâclés et peu pertinents.
* Que faire pour identifier si des champs ont été oubliés lors de l’extraction (simple oubli ou lors de l’ajout de nouveaux champs par la suite) ?
* Certaines limites lors de l’extraction (via JSONPath par exemple) ne permettent pas de vérifier la coherence entre plusieurs champs contrôlés séparément (sans complexifier grandement l’écriture).
* Il peut être difficile d’avoir une vision d’ensemble de la réponse attendue et de comprendre comment elle est contrôlée.

### Comment la mise en place de ces tests pourrait être plus simple et efficace ?
L’idéal lorsque l’on rédige un test serait d’avoir à en faire le minimum tout en testant le maximum de choses. C’est d’autant plus frustrant lorsque la réponse retournée est déterminée et invariante d’avoir à écrire tout un tas de code pour en vérifier son contenu alors qu’on pourrait dire : le corps réponse doit être égal à un corps réponse de référence.
Nous n’aurions alors rien à coder en dehors de la mécanique d’appel à l’API.

Mais dans la pratique ce n’est pas toujours aussi simple, les corps de réponse ne sont pas toujours déterminés et invariables, ce qui complique grandement la comparaison avec une référence.

Il nous faudrait donc un outil nous permettant pour nos tests :
* De rédiger le moins de code possible et rapidement
* De les rendre faciles à lire et comprendre
* De permettre une certaine souplesse d’adaptation en cas de contenu variable
* De pouvoir si besoin vérifier l’exhaustivité du corps de la réponse

## {#JCV#} pour vous aider

### Qu’est ce que {#JCV#} ?
{#JCV#} (acronyme pour “JSON Content Validator”) est une librairie de tests développée par ekino (disponible sur GitHub : [ekino/jcv](https://github.com/ekino/jcv)) permettant de comparer un contenu JSON avec un autre en encapsulant des validateurs à l’intérieur même du JSON de référence.

### Exemple
Si votre corps réponse d’API est :

```json
{
    "field_1": "some value",
    "field_2": "3716a0cf-850e-46c3-bd97-ac1f34437c43",
    "date": "2011-12-03T10:15:30Z",
    "other_fields": [{
        "id": "2",
        "link": "https://another.url.com/my-base-path/query?param1=true"
    }, {
        "id": "1",
        "link": "https://some.url.com"
    }]
}
```

Voici comment vous pouvez la valider :
```json
{
   "field_1": "some value",
   "field_2": "{#uuid#}",
   "date": "{#date_time_format:iso_instant#}",
   "other_fields": [{
       "id": "1",
       "link": "{#url#}"
   }, {
       "id": "2",
       "link": "{#url_ending:query?param1=true#}"
   }]
}
```

On constate alors que certaines valeurs doivent être égales (comme pour le champs `field_1`), mais que d’autres ont des validateurs pour contrôler des valeurs qui pourraient varier (comme pour les champs `field_2`, `date` et `link`).

Cette librairie est basée sur l’excellente librairie [JSONassert](https://github.com/skyscreamer/JSONassert) à laquelle nous avons ajouté la notion de validateur encapsulé dans le contenu du JSON.

### Quels sont les avantages apportés par cet outil ?
Cet outil nous permet donc de répondre aux points suivants :
* __Rapide et facile à rédiger__ : Vous pouvez récupérer le corps de la réponse générée par l’API (via des logs du test par exemple) et l’utiliser directement comme référence.
* __Facile à lire et comprendre__ : La réponse est entière et donne une idée claire du contenu de la réponse au point de pouvoir même servir d’exemple de documentation.
* __Souplesse d’adaptation__ : Pour les valeurs qui varient en fonction de chaque exécution (identifiant généré aléatoirement, date d’exécution, etc.), vous pouvez profiter des validateurs intégrés (ou faire vos propres validateurs) pour valider le format ou certaines règles de gestion particulières.
* __Vérifier l’exhaustivité, la cohérence entre les champs__ : La configuration vous permet de spécifier si le corps peut contenir des champs supplémentaires ou non par rapport à la référence et/ou si les valeurs des éléments d’un tableau doivent respecter l’ordre (fonctionnalités héritées de JSONassert).

Maintenant que nous avons fait connaissance avec {#JCV#}, passons à un cas pratique pour voir comment mettre en place l’outil et l’utiliser.

## De la théorie à la pratique

### Présentation du cas pratique
Prenons un cas pratique en exemple pour voir une des possibilités de mises en oeuvre de ces tests d’API en appliquant ce qui a été vu précédemment.

Nous allons exposer une API très simple qui retournera un corps de réponse qu’il faudra tester. Celui-ci aura la particularité d’avoir un contenu variable entre chaque exécution de test que nous pourrons tout de même valider avec les validateurs encapsulés.

La stack technique du projet utilisée pour l’exemple sera la suivante :
* [Java](https://www.java.com/) 8+
* [Gradle](https://gradle.org/) 5.3+
* [Spring Boot](https://spring.io/projects/spring-boot) 2.1+
* [JUnit](https://junit.org/junit5/) 5.4+
* [RESTAssured](http://rest-assured.io/) 3.3+
* [JSONassert](https://github.com/skyscreamer/JSONassert) 1.5+
* [Hamcrest](http://hamcrest.org/JavaHamcrest/) 2.1+
* [JCV](https://github.com/ekino/jcv) 1.2+

### L’API REST à tester
Pour l’exemple, imaginons que notre API soit utilisée pour récupérer le détail d’une commande en la référençant par son identifiant technique.

L’idée serait de l’appeler via un GET sur le chemin suivant : `/orders/{id}` et d’obtenir une réponse en statut 200 (qui signifie “OK”) et un corps contenant le détail d’une commande constituée de références, de dates et d’une liste d’éléments correspondant à cette commande.

Exemple :
```
GET http://localhost:8080/orders/555cb3a5-74a6-4e33-854a-9b8c7c712b92
HTTP/1.1 200 
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Date: Fri, 12 Apr 2019 12:44:52 GMT

{
    "id": "555cb3a5-74a6-4e33-854a-9b8c7c712b92",
    "reference": "SOME_REF",
    "createdDate": "2019-04-12T12:44:52.123Z",
    "shippingDate": "2019-04-23T10:00:00+02:00",
    "items": [
        {
            "id": "05170f42-7972-4552-9f0d-334567109984",
            "reference": "ITEM_1"
        },
        {
            "id": "0df81fae-d780-4a6c-b51e-61cbd0a69aa7",
            "reference": "ITEM_2"
        },
        {
            "id": "d6ecd6a5-ae6b-4eda-a944-e78f788011c4",
            "reference": "ITEM_9999"
        }
    ]
}
```

Pour illustrer au mieux le cas pratique, cette réponse fictive retourne certaines valeurs de manière constante (les champs `reference`, `shippingDate` et `id` de certains items) tandis que d’autres sont variables (les champs `createdDate`, `id` de l’élement `ITEM_2`).

Pour cela, sans rentrer dans les détails, voici le code du controller de l’API :
```java
import com.ekino.oss.jcv.example.jcvrestassuredexample.dto.OrderDto;
import com.ekino.oss.jcv.example.jcvrestassuredexample.dto.OrderItemDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@RequestMapping(path = "/orders")
@RestController
public class OrderController {

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Object getOrderById(@PathVariable UUID id) {
        return generateOrderMock(id);
    }

    private static OrderDto generateOrderMock(UUID id) {
        return OrderDto.builder()
            .id(id)
            .reference("SOME_REF")
            .createdDate(Instant.now())
            .shippingDate(ZonedDateTime.of(
                LocalDateTime.of(2019, 4, 23, 10, 0),
                ZoneId.of("Europe/Paris")
            ))
            .items(generateItems())
            .build();
    }

    private static List<OrderItemDto> generateItems() {
        return Stream.of(
            OrderItemDto.builder()
                .id(UUID.fromString("05170f42-7972-4552-9f0d-334567109984"))
                .reference("ITEM_1")
                .build(),
            OrderItemDto.builder()
                .id(UUID.randomUUID())
                .reference("ITEM_2")
                .build(),
            OrderItemDto.builder()
                .id(UUID.fromString("d6ecd6a5-ae6b-4eda-a944-e78f788011c4"))
                .reference("ITEM_9999")
                .build()
        )
            .collect(toList());
    }
}
```

Le plus dur est fait ! Vous avez créé votre API REST et elle fonctionne. Mais maintenant il faut écrire les tests pour vous assurer de son bon fonctionnement, son évolutivité ainsi que sa maintenabilité, ce qui d’habitude peut vous fatiguer d’avance rien que d’y penser. Mais plus maintenant, grâce à JCV !

### Installation de {#JCV#}
{#JCV#} est disponible sur Maven Central en 3 modules distincts (voir le [Quick start](https://github.com/ekino/jcv#quick-start) pour plus d’infos) et dans notre situation le module `jcv-hamcrest` est le plus indiqué pour être combiné avec RESTAssured qui utilise des matchers Hamcrest.

Le code source est disponible sur [jcv-restassured-example](https://github.com/ekino/jcv-examples/tree/master/jcv-restassured-example) ou vous pouvez initialiser un nouveau projet via ce [Spring Initializr](https://start.spring.io/#!language=java&javaVersion=8&type=gradle-project) (en ajoutant la dépendance Web).

Dans le build.gradle assurez-vous d’avoir les dépendances suivantes pour vos tests :
```groovy
dependencies {

    // ...

    testImplementation('org.junit.jupiter:junit-jupiter:5.4.2')
    testImplementation('org.springframework.boot:spring-boot-starter-test')
    testImplementation('io.rest-assured:rest-assured:3.3.0')
    testImplementation('org.skyscreamer:jsonassert:1.5.0')
    testImplementation('org.hamcrest:hamcrest:2.1')
    testImplementation('com.ekino.oss.jcv:jcv-hamcrest:1.2.0')
    testImplementation('commons-io:commons-io:2.6')
}
```

Voici le rôle de chacune :
* __junit-jupiter__ :  JUnit, le célèbre framework de test pour Java
* __spring-boot-starter-test__ : Tout ce qu’il faut pour charger et tester une application Spring Boot
* __rest-assured__ : Librairie pour tester des services REST
* __hamcrest__ : Librairie de “matchers” pour rédiger ses tests
* __jcv-hamcrest__ : Module JCV adapté aux matchers Hamcrest
* __commons-io__ : Librairie de manipulation de données d’entrées/sorties (utile pour charger le contenu des réponses dans les ressources de test)

### Mise en place des tests
Une des méthodes très simple à employer est de :
1. Commencer par rédiger l’appel à l’API afin de laisser le test vous montrer la réponse générée
2. Vérifier que le contenu est bien celui attendu
3. L’utiliser comme réponse de référence (avec quelques ajustements possibles pour les réponses variables que nous verrons par la suite).
4. C’est tout… vraiment.

#### Générer la réponse de référence
Voici un exemple de test JUnit pour démarrer votre application Spring Boot dans un contexte de test en utilisant RESTAssured pour faire appel à votre API et faire quelques vérifications sur la réponse renvoyée.
```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static io.restassured.RestAssured.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerTest {

    @LocalServerPort
    private Integer serverPort;

    @DisplayName("Default : Test non-extensible body without ordering in arrays")
    @Test
    void shouldGetOrderById() {

        given()
            .port(serverPort)
            .get("/orders/555cb3a5-74a6-4e33-854a-9b8c7c712b92")
            .then()
            .log().all()
            .statusCode(HttpStatus.OK.value());
    }
}
```

Jusqu’ici rien d’extraordinaire, c’est un test JUnit pour Spring Boot classique avec un appel `GET` effectué sur `/orders/555cb3a5-74a6-4e33-854a-9b8c7c712b92` qui vérifie que la réponse renvoyée est bien en statut “OK”.
N’oubliez pas d’ajouter le `.log().all()` pour afficher le contenu de la réponse que nous allons réutiliser par la suite.

Exécutons le test et nous obtenons quelque chose de ce style :
```json
{
    "id": "555cb3a5-74a6-4e33-854a-9b8c7c712b92",
    "reference": "SOME_REF",
    "createdDate": "2019-04-12T13:12:28.758Z",
    "shippingDate": "2019-04-23T10:00:00+02:00",
    "items": [
        {
            "id": "05170f42-7972-4552-9f0d-334567109984",
            "reference": "ITEM_1"
        },
        {
            "id": "b6465714-e29c-4d02-93d1-750bfcbb6a0b",
            "reference": "ITEM_2"
        },
        {
            "id": "d6ecd6a5-ae6b-4eda-a944-e78f788011c4",
            "reference": "ITEM_9999"
        }
    ]
}
```

#### Valider le test auprès de la réponse de référence
Nous pouvons maintenant utiliser la réponse que nous venons de générer pour comparer la réponse du test lors des prochaines exécutions. Pour cela, il suffit d’utiliser les utilitaires fournis par le module jcv-hamcrest, en ajouter simplement une ligne au test précédent :
```java
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;

import static com.ekino.oss.jcv.assertion.hamcrest.JsonMatchers.*;

...

void shouldGetOrderById() {

   given()
       .port(serverPort)
       .get("/orders/555cb3a5-74a6-4e33-854a-9b8c7c712b92")
       .then()
       .statusCode(HttpStatus.OK.value())
       .body(jsonMatcher(loadJson("get_order_by_id_expected.json")));
}

private static String loadJson(String filename) {
   try {
       return IOUtils.resourceToString(
           Paths.get("/controller/orders", filename).toString(),
           StandardCharsets.UTF_8
       );
   } catch (IOException e) {
       throw new UncheckedIOException(e);
   }
}
```

Dans cette nouvelle ligne ajoutée nous avons : `body(jsonMatcher(loadJson("get_order_by_id_expected.json")))`
* __body__(...) : Est une méthode de RESTAssured qui attend un “matcher” Hamcrest afin de vérifier le contenu de la réponse renvoyée.
* __jsonMatcher__(...) : Est une méthode du module jcv-hamcrest qui prend un JSON de référence en paramètre.
* __loadJson__(...) : Est une petite méthode utilitaire permettant de récupérer le JSON de référence dans un fichier placé dans les ressources de test. Dans notre exemple, le fichier se trouve dans le répertoire `src/test/resources/controller/orders/get_order_by_id_expected.json`.

Exécutons le test à nouveau pour voir le résultat :
```
1 expectation failed.
Response body doesn't match expectation.
Expected: createdDate
Expected: 2019-04-12T13:12:28.758Z
     got: 2019-04-12T13:32:36.256Z
 ; items[id=b6465714-e29c-4d02-93d1-750bfcbb6a0b]
Expected: a JSON object
     but none found
 ; items[id=c2417d68-c985-414c-b744-00a48f93ef8a]
Unexpected: a JSON object

  Actual: {"id":"555cb3a5-74a6-4e33-854a-9b8c7c712b92","reference":"SOME_REF","createdDate":"2019-04-12T13:32:36.256Z","shippingDate":"2019-04-23T10:00:00+02:00","items":[{"id":"05170f42-7972-4552-9f0d-334567109984","reference":"ITEM_1"},{"id":"c2417d68-c985-414c-b744-00a48f93ef8a","reference":"ITEM_2"},{"id":"d6ecd6a5-ae6b-4eda-a944-e78f788011c4","reference":"ITEM_9999"}]}
```

Comme nous pouvons le constater le résultat est en erreur pour plusieurs raisons :
* Le champ `createdDate` ne possède pas la même valeur que celle de la réponse de référence : en effet la date est différente lors de chaque appel.
* Un objet `item` avec le champ `id` valant `b6465714-e29c-4d02-93d1-750bfcbb6a0b` n’a pas été trouvé et en revanche, un objet `item` avec le champ id valant `c2417d68-c985-414c-b744-00a48f93ef8a` n’était pas attendu : en effet l’id d’un des éléments `item` est généré aléatoirement à chaque appel.
* Le reste est bien conforme à la réponse de référence.

En plus d’indiquer, de manière assez détaillée, les différences entre la réponse reçue et celle de référence, le listing des erreurs est le plus complet possible en un seul test.

Il faut donc maintenant adapter le contenu de la réponse de référence afin de pouvoir valider les éléments variables au fil des exécutions successives en indiquant par exemple que :
* Le champ `createdDate` possède une valeur au format date [ISO 8601](https://fr.wikipedia.org/wiki/ISO_8601).
* Le champ `id` d’un des éléments `item` possède une valeur au format [UUID](https://fr.wikipedia.org/wiki/Universal_Unique_Identifier)

Pour cela, nous avons besoin de validateurs comme ceux fournis par JCV.

#### Adapter la réponse de référence avec des validateurs
Une liste de validateurs est disponible directement dans le module jcv que nous avons utilisé depuis le début. Une liste exhaustive avec des exemples est disponible sur le wiki de la librairie : [Validateurs JCV](https://github.com/ekino/jcv/wiki/Predefined-validators).

Un validateur JCV se déclare de la façon suivante :
* Le référencement d’un validateur se fait dans la valeur d’un champ du JSON
* La valeur du champ doit respecter le format suivant : `{#idenfitiant_du_validateur#}`
* Si ce validateur doit recevoir des paramètres, il faut ajouter `:` après l’identifiant du validateur, ajouter ensuite les paramètres séparés par des `;`.
Exemple : `{#nom_du_validateur:param_1;param_2#}`

Dans notre exemple, voici les 2 validateurs qui vont nous intéresser : [uuid](https://github.com/ekino/jcv/wiki/Predefined-validators#uuid) et [date_time_format](https://github.com/ekino/jcv/wiki/Predefined-validators#date_time_format).

Pour le champ `id`, rien de plus simple car le validateur n’attend aucun paramètre et peut être déclaré comme ceci : `“id”: “{#uuid#}`.

Pour le champ `createdDate`, le validateur de date a besoin en paramètre de savoir quel est le format de date à valider. Dans la documentation qui liste les différents formats possibles, le format `iso_instant` correspond à ce que nous cherchons et se déclare donc comme ceci : `createdDate”: “{#date_time_format:iso_instant#}`.

Si nous appliquons ces changements à notre réponse de référence, voici à quoi elle doit maintenant ressembler :
```json
{
  "id": "555cb3a5-74a6-4e33-854a-9b8c7c712b92",
  "reference": "SOME_REF",
  "createdDate": "{#date_time_format:iso_instant#}",
  "shippingDate": "2019-04-23T10:00:00+02:00",
  "items": [
    {
      "id": "05170f42-7972-4552-9f0d-334567109984",
      "reference": "ITEM_1"
    },
    {
      "id": "{#uuid#}",
      "reference": "ITEM_2"
    },
    {
      "id": "d6ecd6a5-ae6b-4eda-a944-e78f788011c4",
      "reference": "ITEM_9999"
    }
  ]
}
```

Ré-exécutons le test, et celui-ci passe maintenant avec succès.

Voilà, c’est maintenant aussi simple que ça d’écrire ses tests d’API.

### Aller plus loin

Sachez que vous pouvez également configurer l’utilitaire pour :
* Permettre à la réponse de contenir des champs supplémentaires par rapport à la réponse de référence (vérification stricte par défaut).
* Permettre de conserver l’ordre des éléments dans les tableaux (ordre non-strict par défaut)
* Définir vos propres validateurs pour servir au mieux votre besoin.

Des documentations sont disponibles dans le [“README” du projet](https://github.com/ekino/jcv/blob/master/README.md), dans le [Wiki du projet](https://github.com/ekino/jcv/wiki)  et également des exemples sur les [différentes configurations possibles](https://github.com/ekino/jcv-examples/blob/master/jcv-restassured-example/src/test/java/com/ekino/oss/jcv/example/jcvrestassuredexample/controller/OrderControllerTest.java) et la [création de validateurs personnalisés](https://github.com/ekino/jcv-examples/tree/master/jcv-customvalidator-example/src/test/java/com/ekino/oss/jcv/example/jcvcustomvalidatorexample).
