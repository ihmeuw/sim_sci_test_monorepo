============
Installation
============

Requirements
============

* Python 3.10 or 3.11
* sim-sci-test-monorepo-core (dependency)
* Dependencies listed in pyproject.toml

Installation from PyPI
======================

.. code-block:: bash

   pip install sim-sci-test-monorepo-public-health

This will automatically install the core package as a dependency.

Development Installation
========================

To install in development mode:

.. code-block:: bash

   git clone https://github.com/ihmeuw/sim_sci_test_monorepo.git
   cd sim_sci_test_monorepo/libs/public_health
   pip install -e .