# README — Projet "Sauve le hérisson!"

**Auteurs :** BOUGRAOU Marouane · ELMAWLA Yara

---

## 1. Modifications de l'architecture

### 1.1 Hiérarchie des classes

L'architecture du projet a été enrichie avec plusieurs nouvelles hiérarchies de classes pour répondre aux besoins du jeu.

#### Hiérarchie des insectes

- **`Insect`** *(abstrait)* — Classe de base pour tous les insectes, implémentant `Movable`
  - **`Wasp`** — Guêpes avec 1 point de vie, infligeant 20 points de dégâts
  - **`Hornet`** — Frelons avec 2 points de vie, infligeant 30 points de dégâts

Cette hiérarchie permet de factoriser le comportement commun (déplacement aléatoire, collision) tout en permettant aux sous-classes de spécifier leurs caractéristiques propres (résistance, dégâts).

#### Hiérarchie des nids

- **`NestInsect`** *(abstrait)* — Classe de base pour tous les nids d'insectes
  - **`NestWasp`** — Génère des guêpes et une bombe toutes les 5 secondes
  - **`NestHornet`** — Génère des frelons et deux bombes toutes les 10 secondes

Cette conception permet de réutiliser la logique de génération tout en spécialisant chaque type de nid.

#### Système de bonus étendu

- **`Bonus`** *(existant)* — Classe de base pour tous les bonus
  - **`EnergyBoost`** — Pommes qui restaurent l'énergie et guérissent
  - **`PoisonedApple`** *(ajouté)* — Pommes empoisonnées qui augmentent le niveau de maladie
  - **`Carrots`** *(ajouté)* — Carottes à collecter pour ouvrir les portes
  - **`Insecticide`** *(ajouté)* — Bombes pour combattre les insectes

#### Nouveaux décors

- **`Door`** — Portes entre les niveaux, avec état ouvert/fermé
- **`Flowers`** — Fleurs traversables uniquement par les insectes
- **`Hedgehog`** — Le hérisson à retrouver (objectif du jeu)

---

### 1.2 Justification des choix architecturaux

1. **Pattern Visiteur** — Le double dispatch a été maintenu et étendu pour les interactions entre le jardinier et les différents types de bonus et décors, ce qui permet une extensibilité facile.

2. **Abstraction** — Les classes abstraites (`Insect`, `NestInsect`) factorisent le comportement commun pour simplifier l'ajout de nouveaux types d'insectes ou de nids dans le futur.

3. **Composition vs Héritage** — La composition a été utilisée lorsque cela était possible (ex : les bonus sont liés à un décor) pour éviter les problèmes classiques de l'héritage multiple.

4. **Séparation des préoccupations** — La logique d'affichage (sprites) reste séparée de la logique de jeu (objets), permettant de modifier l'un sans impacter l'autre.

5. **Timers** — L'utilisation de la classe `Timer` a été généralisée pour gérer les aspects temporels (génération d'insectes, récupération d'énergie, durée des maladies), assurant une cohérence dans la gestion du temps.

---

## 2. État du projet

### 2.1 Fonctionnalités implémentées et fonctionnelles

- [x] Déplacement du jardinier avec blocage sur les obstacles
- [x] Système d'énergie (consommation et récupération)
- [x] Système de maladie (pommes empoisonnées)
- [x] Ramassage des carottes et ouverture des portes
- [x] Génération et comportement des guêpes
- [x] Génération et comportement des frelons (plus résistants)
- [x] Système de bombes insecticides
- [x] Gestion des niveaux multiples et transitions
- [x] Conditions de victoire (trouver le hérisson) et défaite (perte d'énergie)
- [x] Affichage des informations dans la barre d'état
- [x] Chargement de cartes depuis des fichiers

### 2.2 Limitations connues

- Le comportement des insectes est purement aléatoire, sans intelligence pour poursuivre le jardinier
- Le mécanisme de génération des bombes insecticides peut parfois échouer si l'espace est encombré
- Les changements de niveau peuvent occasionnellement causer des problèmes d'affichage qui nécessitent un redémarrage
- Il n'existe pas de mécanisme de sauvegarde de la progression

### 2.3 Notes techniques supplémentaires

- La configuration du jeu peut être modifiée via le fichier `properties`
- Le jeu supporte le format de carte compressé pour des niveaux plus compacts
- Des messages sont affichés pour guider le joueur lors d'événements importants
- Le code est commenté pour faciliter sa compréhension et sa maintenance future
