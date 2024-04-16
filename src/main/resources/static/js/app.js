function handleEvent(eventType, selector, handler) {
  document.addEventListener(eventType, function(event) {
    if (event.target.matches(selector + ', ' + selector + ' *')) {
      handler.apply(event.target.closest(selector), arguments);
    }
  });
}

handleEvent('submit', '.js-submit-confirm', function(event) {
  if (!confirm(this.getAttribute('data-confirm-message'))) {
    event.preventDefault();
    return false;
  }
  return true;
});

document.addEventListener('DOMContentLoaded', function() {
    var filterForm = document.getElementById('filterForm');
    var jobTypeFilters = document.querySelectorAll('.jobTypeFilter');
    var jobLocationFilters = document.querySelectorAll('.jobLocationFilter');

    function applyFilters() {
        filterForm.submit();
    }

    jobTypeFilters.forEach(function(filter) {
        filter.addEventListener('change', applyFilters);
    });

    jobLocationFilters.forEach(function(filter) {
        filter.addEventListener('change', applyFilters);
    });
});