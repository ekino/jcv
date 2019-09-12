---
title: Articles
---

# {{ page.title }}

<ul>
  {% for post in site.posts %}
    <li>
      <a href="{{ post.url | relative_url }}">{{ post.title }}</a>
      <small>{{ post.date | date_to_long_string }} - by {{ post.author }}</small>
    </li>
  {% endfor %}
</ul>
