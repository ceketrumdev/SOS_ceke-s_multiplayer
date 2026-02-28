# **************************************************************************** #
#                                                                              #
#       ::::::::  :::::::::: :::    ::: ::::::::::                           #
#     :+:    :+: :+:        :+:   :+:  :+:                                   #
#    +:+        +:+        +:+  +:+   +:+                                    #
#   +#+        +#++:++#   +#++:++    +#++:++#                                #
#  +#+        +#+        +#+  +#+   +#+                                      #
# #+#    #+# #+#        #+#   #+#  #+#                                       #
# ########  ########## ###    ### ##########                                 #
#                                                                              #
#   increment-build.ps1                                                      #
#                                                                              #
#   By: ceketrum <ferrando.ryan.mickael@gmail.com>                           #
#                                                                              #
#   Created: 2026/02/28 15:02:29 by ceketrum                                 #
#   Updated: 2026/02/28 15:02:29 by ceketrum                                 #
#                                                                              #
# **************************************************************************** #

$f = Join-Path $PSScriptRoot "version.properties"
$c = (Get-Content $f -Raw).Trim()
$n = [int]($c -replace 'build.number=', '') + 1
Set-Content $f "build.number=$n"
Write-Host "Build number incremented to: $n"
