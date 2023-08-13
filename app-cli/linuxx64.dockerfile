FROM archlinux:base

RUN pacman -Syuu --noconfirm libxcrypt-compat

COPY cli-linux.kexe /opt/app/app.kexe
ENTRYPOINT /opt/app/app.kexe
