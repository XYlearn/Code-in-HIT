from django.contrib import admin
from django.contrib.admin.models import LogEntry

# admin.site.register(LogEntry)
@admin.register(LogEntry)
class LogEntryAdmin(admin.ModelAdmin):
    readonly_fields = [
        'action_time', 'user', 'content_type', 'object_id', 
        'object_repr', 'action_flag', 'change_message', 'objects']
