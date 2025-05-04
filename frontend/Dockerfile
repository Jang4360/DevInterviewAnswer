FROM node:18
WORKDIR /app
COPY . .
RUN npm install --omit=dev
RUN npm run build
EXPOSE 3000
CMD ["npx","next","start"]
