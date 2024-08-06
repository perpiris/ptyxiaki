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
    let filterForm = document.getElementById('filterForm');
    let jobTypeFilters = document.querySelectorAll('.jobTypeFilter');
    let jobLocationFilters = document.querySelectorAll('.jobLocationFilter');
    let matchMySkillsCheckbox = document.getElementById('matchMySkills');

    function applyFilters() {
        filterForm.submit();
    }

    jobTypeFilters.forEach(function(filter) {
        filter.addEventListener('change', applyFilters);
    });

    jobLocationFilters.forEach(function(filter) {
        filter.addEventListener('change', applyFilters);
    });

    if (matchMySkillsCheckbox) {
        matchMySkillsCheckbox.addEventListener('change', applyFilters);
    }
});